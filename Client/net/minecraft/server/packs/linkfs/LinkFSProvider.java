/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.lang.Class
 *  java.lang.NullPointerException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.UnsupportedOperationException
 *  java.net.URI
 *  java.nio.channels.SeekableByteChannel
 *  java.nio.file.AccessDeniedException
 *  java.nio.file.AccessMode
 *  java.nio.file.CopyOption
 *  java.nio.file.DirectoryIteratorException
 *  java.nio.file.DirectoryStream
 *  java.nio.file.DirectoryStream$Filter
 *  java.nio.file.FileStore
 *  java.nio.file.FileSystem
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.NoSuchFileException
 *  java.nio.file.NotDirectoryException
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.ProviderMismatchException
 *  java.nio.file.ReadOnlyFileSystemException
 *  java.nio.file.StandardOpenOption
 *  java.nio.file.attribute.BasicFileAttributeView
 *  java.nio.file.attribute.BasicFileAttributes
 *  java.nio.file.attribute.FileAttribute
 *  java.nio.file.attribute.FileAttributeView
 *  java.nio.file.spi.FileSystemProvider
 *  java.util.Iterator
 *  java.util.Map
 *  java.util.Set
 *  javax.annotation.Nullable
 */
package net.minecraft.server.packs.linkfs;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.server.packs.linkfs.LinkFSPath;
import net.minecraft.server.packs.linkfs.PathContents;

class LinkFSProvider
extends FileSystemProvider {
    public static final String SCHEME = "x-mc-link";

    LinkFSProvider() {
    }

    public String getScheme() {
        return SCHEME;
    }

    public FileSystem newFileSystem(URI $$0, Map<String, ?> $$1) {
        throw new UnsupportedOperationException();
    }

    public FileSystem getFileSystem(URI $$0) {
        throw new UnsupportedOperationException();
    }

    public Path getPath(URI $$0) {
        throw new UnsupportedOperationException();
    }

    public SeekableByteChannel newByteChannel(Path $$0, Set<? extends OpenOption> $$1, FileAttribute<?> ... $$2) throws IOException {
        if ($$1.contains((Object)StandardOpenOption.CREATE_NEW) || $$1.contains((Object)StandardOpenOption.CREATE) || $$1.contains((Object)StandardOpenOption.APPEND) || $$1.contains((Object)StandardOpenOption.WRITE)) {
            throw new UnsupportedOperationException();
        }
        Path $$3 = LinkFSProvider.toLinkPath($$0).toAbsolutePath().getTargetPath();
        if ($$3 == null) {
            throw new NoSuchFileException($$0.toString());
        }
        return Files.newByteChannel((Path)$$3, $$1, $$2);
    }

    public DirectoryStream<Path> newDirectoryStream(Path $$0, final DirectoryStream.Filter<? super Path> $$1) throws IOException {
        final PathContents.DirectoryContents $$2 = LinkFSProvider.toLinkPath($$0).toAbsolutePath().getDirectoryContents();
        if ($$2 == null) {
            throw new NotDirectoryException($$0.toString());
        }
        return new DirectoryStream<Path>(){

            public Iterator<Path> iterator() {
                return $$2.children().values().stream().filter($$1 -> {
                    try {
                        return $$1.accept($$1);
                    }
                    catch (IOException $$22) {
                        throw new DirectoryIteratorException($$22);
                    }
                }).map($$0 -> $$0).iterator();
            }

            public void close() {
            }
        };
    }

    public void createDirectory(Path $$0, FileAttribute<?> ... $$1) {
        throw new ReadOnlyFileSystemException();
    }

    public void delete(Path $$0) {
        throw new ReadOnlyFileSystemException();
    }

    public void copy(Path $$0, Path $$1, CopyOption ... $$2) {
        throw new ReadOnlyFileSystemException();
    }

    public void move(Path $$0, Path $$1, CopyOption ... $$2) {
        throw new ReadOnlyFileSystemException();
    }

    public boolean isSameFile(Path $$0, Path $$1) {
        return $$0 instanceof LinkFSPath && $$1 instanceof LinkFSPath && $$0.equals((Object)$$1);
    }

    public boolean isHidden(Path $$0) {
        return false;
    }

    public FileStore getFileStore(Path $$0) {
        return LinkFSProvider.toLinkPath($$0).getFileSystem().store();
    }

    public void checkAccess(Path $$0, AccessMode ... $$1) throws IOException {
        if ($$1.length == 0 && !LinkFSProvider.toLinkPath($$0).exists()) {
            throw new NoSuchFileException($$0.toString());
        }
        block4: for (AccessMode $$2 : $$1) {
            switch ($$2) {
                case READ: {
                    if (LinkFSProvider.toLinkPath($$0).exists()) continue block4;
                    throw new NoSuchFileException($$0.toString());
                }
                case EXECUTE: 
                case WRITE: {
                    throw new AccessDeniedException($$2.toString());
                }
            }
        }
    }

    @Nullable
    public <V extends FileAttributeView> V getFileAttributeView(Path $$0, Class<V> $$1, LinkOption ... $$2) {
        LinkFSPath $$3 = LinkFSProvider.toLinkPath($$0);
        if ($$1 == BasicFileAttributeView.class) {
            return (V)$$3.getBasicAttributeView();
        }
        return null;
    }

    public <A extends BasicFileAttributes> A readAttributes(Path $$0, Class<A> $$1, LinkOption ... $$2) throws IOException {
        LinkFSPath $$3 = LinkFSProvider.toLinkPath($$0).toAbsolutePath();
        if ($$1 == BasicFileAttributes.class) {
            return (A)$$3.getBasicAttributes();
        }
        throw new UnsupportedOperationException("Attributes of type " + $$1.getName() + " not supported");
    }

    public Map<String, Object> readAttributes(Path $$0, String $$1, LinkOption ... $$2) {
        throw new UnsupportedOperationException();
    }

    public void setAttribute(Path $$0, String $$1, Object $$2, LinkOption ... $$3) {
        throw new ReadOnlyFileSystemException();
    }

    private static LinkFSPath toLinkPath(@Nullable Path $$0) {
        if ($$0 == null) {
            throw new NullPointerException();
        }
        if ($$0 instanceof LinkFSPath) {
            LinkFSPath $$1 = (LinkFSPath)$$0;
            return $$1;
        }
        throw new ProviderMismatchException();
    }
}