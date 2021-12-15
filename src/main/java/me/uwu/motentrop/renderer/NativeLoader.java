package me.uwu.motentrop.renderer;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Log4j2
public enum NativeLoader {
    INSTANCE;

    private final SystemEnvironment systemEnvironment;
    private final File nativeRoot;

    NativeLoader() {
        this.systemEnvironment = SystemEnvironment.getEnvironment();

        String temp = System.getProperty("java.io.tmpdir");
        File tempDirectory = new File(temp);
        if (!tempDirectory.exists()) {
            tempDirectory = new File("/tmp");
            if (!tempDirectory.mkdirs()) {
                CodeSource source = getClass().getProtectionDomain().getCodeSource();
                if (source != null) {
                    URL location = source.getLocation();
                    if (location != null) {
                        File currentDir = new File(location.getFile());
                        if (currentDir.isFile()) currentDir = currentDir.getParentFile();
                        tempDirectory = currentDir;
                        if (!tempDirectory.exists()) {
                            throw new RuntimeException("Couldn't get or create temporary directory");
                        }
                    } else {
                        throw new RuntimeException("Couldn't get or create temporary directory");
                    }
                } else {
                    throw new RuntimeException("Couldn't get or create temporary directory");
                }
            }
        }

        this.nativeRoot = new File(
                temp,
                "lwjgl-natives" + File.separator
                        + this.systemEnvironment.operatingSystem.identifier + File.separator
                        + this.systemEnvironment.architecture.name + File.separator
        );
        assert this.nativeRoot.mkdirs() && this.nativeRoot.exists();

        String userPath = System.getProperty("java.library.path");
        String libDirPath = nativeRoot.getAbsolutePath();
        if (userPath == null || userPath.isEmpty()) {
            userPath = libDirPath;
        } else {
            if (!userPath.contains(libDirPath)) {
                userPath += File.pathSeparator + libDirPath;
            }
        }
        System.setProperty("java.library.path", userPath);

        refreshNativeCache();
    }

    private void refreshNativeCache() {
        try {
            Class<ClassLoader> classLoaderClass = ClassLoader.class;
            Field fieldSysPaths = classLoaderClass.getDeclaredField("sys_paths");
            fieldSysPaths.setAccessible(true);
            fieldSysPaths.set(null, null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Couldn't invalidate VM native cache", e);
        }
    }

    public void extractLWJGLNatives() {
        SystemEnvironment.OperatingSystem os = this.systemEnvironment.operatingSystem;
        URLClassLoader urlClassLoader = (URLClassLoader) this.getClass().getClassLoader();

        for (URL url : urlClassLoader.getURLs()) {
            File file = new File(url.getFile());
            if (file.exists() && file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".jar")) {
                try (ZipFile zipFile = new ZipFile(file)) {
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        String name = entry.getName().toLowerCase(Locale.ROOT);
                        if (name.startsWith("/")) name = name.substring(1);
                        if (name.startsWith(os.nativePrefix) && name.endsWith("." + os.nativeExtension)) {
                            File output = new File(this.nativeRoot, name);
                            if (output.exists()) {
                                log.info(name + " already extracted, skipping...");
                                continue;
                            }

                            log.info("Extracting " + name + "...");
                            output.getParentFile().mkdirs();
                            output.createNewFile();

                            try (InputStream zis = zipFile.getInputStream(entry);
                                 FileOutputStream fos = new FileOutputStream(output)) {
                                int len;
                                byte[] buffer = new byte[4096];
                                while ((len = zis.read(buffer)) > 0) {
                                    fos.write(buffer, 0, len);
                                }
                            } catch (IOException e) {
                                throw new RuntimeException("Error while copying native file.", e);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        refreshNativeCache();
    }

    public @Data
    static class SystemEnvironment {

        private final Architecture architecture;
        private final OperatingSystem operatingSystem;

        public static SystemEnvironment getEnvironment() {
            String osProperty = System.getProperty("os.name").toLowerCase();
            OperatingSystem os = OperatingSystem.UNKNOWN;

            loop:
            for (OperatingSystem potentialOs : OperatingSystem.values()) {
                if (potentialOs.getToSearch().length > 0) {
                    for (String string : potentialOs.getToSearch()) {
                        if (osProperty.contains(string)) {
                            os = potentialOs;
                            break loop;
                        }
                    }
                }
            }

            Architecture arch = Architecture.x64;
            String archProperty = System.getProperty("os.arch").toLowerCase();
            if (os != OperatingSystem.DARWIN) {
                arch = Architecture.x86;

                if (archProperty.contains("amd64") || archProperty.contains("x86_64")) {
                    arch = Architecture.x64;
                }
            } else {
                if (archProperty.contains("arm")) {
                    arch = Architecture.ARM64;
                }
            }

            return new SystemEnvironment(arch, os);
        }

        @Getter
        @RequiredArgsConstructor
        @ToString
        public enum Architecture {
            ARM64("arm64"),
            x64("x86_64"),
            x86("x86");

            private final String name;
        }

        @Getter
        @RequiredArgsConstructor
        @ToString
        public enum OperatingSystem {
            WINDOWS("Windows", "win", new String[]{"win"}, "", "dll"),
            LINUX("Linux", "linux", new String[]{"nux", "nix"}, "lib", "so"),
            SOLARIS("Solaris", "solaris", new String[]{"solaris", "sunos"}, "lib", "so"),
            DARWIN("OSX", "osx", new String[]{"mac", "osx"}, "lib", "dylib"),
            UNKNOWN("Unknown", "unknown", new String[0], "", "");

            private final String name;
            private final String identifier;
            private final String[] toSearch;
            private final String nativePrefix;
            private final String nativeExtension;
        }
    }
}
