package io.github.unapplicable.kubectlCpDistroless.javatar;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

class Javatar {
    public static void main(String[] args) throws IOException {
        if (args.length != 3 || !"cf".equals(args[0]) || !"-".equals(args[1])) {
            throw new IllegalArgumentException("Expected arguments 'cf - <path>', got " + Arrays.toString(args));
        }

        String sourcePath = args[2];
        String prefix = new File(sourcePath).getParentFile().getAbsolutePath() + "/";
        try (TarArchiveOutputStream os = new TarArchiveOutputStream(System.out)) {
            os.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
            addPath(sourcePath, prefix, os);
        }
    }

    private static void addPath(String sourcePath, String prefix, TarArchiveOutputStream os) throws IOException {
        File sourceFile = new File(sourcePath);
        String entryName = prefix + sourceFile.getName();
        os.putArchiveEntry(new TarArchiveEntry(sourceFile, entryName));
        if (sourceFile.isFile()) {
            try (FileInputStream fis = new FileInputStream(sourceFile)) {
                IOUtils.copy(fis, os);
                os.closeArchiveEntry();
            }
        } else if (sourceFile.isDirectory()) {
            os.closeArchiveEntry();
            String subPrefix = entryName + "/";
            for (File subFile : sourceFile.listFiles()) {
                addPath(subFile.getAbsolutePath(), subPrefix, os);
            }
        }
    }
}