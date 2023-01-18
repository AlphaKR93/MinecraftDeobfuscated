/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DataResult
 *  java.io.File
 *  java.io.IOException
 *  java.lang.CharSequence
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.file.FileAlreadyExistsException
 *  java.nio.file.Files
 *  java.nio.file.InvalidPathException
 *  java.nio.file.LinkOption
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.nio.file.attribute.FileAttribute
 *  java.util.Arrays
 *  java.util.List
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 *  org.apache.commons.io.FilenameUtils
 */
package net.minecraft;

import com.mojang.serialization.DataResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.SharedConstants;
import org.apache.commons.io.FilenameUtils;

public class FileUtil {
    private static final Pattern COPY_COUNTER_PATTERN = Pattern.compile((String)"(<name>.*) \\((<count>\\d*)\\)", (int)66);
    private static final int MAX_FILE_NAME = 255;
    private static final Pattern RESERVED_WINDOWS_FILENAMES = Pattern.compile((String)".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", (int)2);
    private static final Pattern STRICT_PATH_SEGMENT_CHECK = Pattern.compile((String)"[-._a-z0-9]+");

    public static String findAvailableName(Path $$0, String $$1, String $$2) throws IOException {
        for (char $$3 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
            $$1 = $$1.replace($$3, '_');
        }
        if (RESERVED_WINDOWS_FILENAMES.matcher((CharSequence)($$1 = $$1.replaceAll("[./\"]", "_"))).matches()) {
            $$1 = "_" + $$1 + "_";
        }
        Matcher $$4 = COPY_COUNTER_PATTERN.matcher((CharSequence)$$1);
        int $$5 = 0;
        if ($$4.matches()) {
            $$1 = $$4.group("name");
            $$5 = Integer.parseInt((String)$$4.group("count"));
        }
        if ($$1.length() > 255 - $$2.length()) {
            $$1 = $$1.substring(0, 255 - $$2.length());
        }
        while (true) {
            String $$6 = $$1;
            if ($$5 != 0) {
                String $$7 = " (" + $$5 + ")";
                int $$8 = 255 - $$7.length();
                if ($$6.length() > $$8) {
                    $$6 = $$6.substring(0, $$8);
                }
                $$6 = $$6 + $$7;
            }
            $$6 = $$6 + $$2;
            Path $$9 = $$0.resolve($$6);
            try {
                Path $$10 = Files.createDirectory((Path)$$9, (FileAttribute[])new FileAttribute[0]);
                Files.deleteIfExists((Path)$$10);
                return $$0.relativize($$10).toString();
            }
            catch (FileAlreadyExistsException $$11) {
                ++$$5;
                continue;
            }
            break;
        }
    }

    public static boolean isPathNormalized(Path $$0) {
        Path $$1 = $$0.normalize();
        return $$1.equals((Object)$$0);
    }

    public static boolean isPathPortable(Path $$0) {
        for (Path $$1 : $$0) {
            if (!RESERVED_WINDOWS_FILENAMES.matcher((CharSequence)$$1.toString()).matches()) continue;
            return false;
        }
        return true;
    }

    public static Path createPathToResource(Path $$0, String $$1, String $$2) {
        String $$3 = $$1 + $$2;
        Path $$4 = Paths.get((String)$$3, (String[])new String[0]);
        if ($$4.endsWith($$2)) {
            throw new InvalidPathException($$3, "empty resource name");
        }
        return $$0.resolve($$4);
    }

    public static String getFullResourcePath(String $$0) {
        return FilenameUtils.getFullPath((String)$$0).replace((CharSequence)File.separator, (CharSequence)"/");
    }

    public static String normalizeResourcePath(String $$0) {
        return FilenameUtils.normalize((String)$$0).replace((CharSequence)File.separator, (CharSequence)"/");
    }

    /*
     * Exception decompiling
     */
    public static DataResult<List<String>> decomposePath(String $$0) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter$TooOptimisticMatchException
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.getString(SwitchStringRewriter.java:404)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.access$600(SwitchStringRewriter.java:53)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter$SwitchStringMatchResultCollector.collectMatches(SwitchStringRewriter.java:368)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.ResetAfterTest.match(ResetAfterTest.java:24)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.KleeneN.match(KleeneN.java:24)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.MatchSequence.match(MatchSequence.java:26)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.ResetAfterTest.match(ResetAfterTest.java:23)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.rewriteComplex(SwitchStringRewriter.java:201)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.rewrite(SwitchStringRewriter.java:73)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:881)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at cuchaz.enigma.source.cfr.CfrSource.ensureDecompiled(CfrSource.java:81)
         *     at cuchaz.enigma.source.cfr.CfrSource.asString(CfrSource.java:50)
         *     at cuchaz.enigma.EnigmaProject$JarExport.decompileClass(EnigmaProject.java:298)
         *     at cuchaz.enigma.EnigmaProject$JarExport.lambda$decompileStream$1(EnigmaProject.java:274)
         *     at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
         *     at java.base/java.util.AbstractList$RandomAccessSpliterator.forEachRemaining(AbstractList.java:722)
         *     at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
         *     at java.base/java.util.stream.ForEachOps$ForEachTask.compute(ForEachOps.java:290)
         *     at java.base/java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:754)
         *     at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:387)
         *     at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1311)
         *     at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1840)
         *     at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1806)
         *     at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:177)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public static Path resolvePath(Path $$0, List<String> $$1) {
        int $$2 = $$1.size();
        return switch ($$2) {
            case 0 -> $$0;
            case 1 -> $$0.resolve((String)$$1.get(0));
            default -> {
                String[] $$3 = new String[$$2 - 1];
                for (int $$4 = 1; $$4 < $$2; ++$$4) {
                    $$3[$$4 - 1] = (String)$$1.get($$4);
                }
                yield $$0.resolve($$0.getFileSystem().getPath((String)$$1.get(0), $$3));
            }
        };
    }

    public static boolean isValidStrictPathSegment(String $$0) {
        return STRICT_PATH_SEGMENT_CHECK.matcher((CharSequence)$$0).matches();
    }

    public static void validatePath(String ... $$0) {
        if ($$0.length == 0) {
            throw new IllegalArgumentException("Path must have at least one element");
        }
        for (String $$1 : $$0) {
            if (!$$1.equals((Object)"..") && !$$1.equals((Object)".") && FileUtil.isValidStrictPathSegment($$1)) continue;
            throw new IllegalArgumentException("Illegal segment " + $$1 + " in path " + Arrays.toString((Object[])$$0));
        }
    }

    public static void createDirectoriesSafe(Path $$0) throws IOException {
        Files.createDirectories((Path)(Files.exists((Path)$$0, (LinkOption[])new LinkOption[0]) ? $$0.toRealPath(new LinkOption[0]) : $$0), (FileAttribute[])new FileAttribute[0]);
    }
}