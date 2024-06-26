package suwayomi.tachidesk.manga.impl.download.fileProvider.impl

import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance
import suwayomi.tachidesk.manga.impl.download.fileProvider.ChaptersFilesProvider
import suwayomi.tachidesk.manga.impl.util.getChapterCachePath
import suwayomi.tachidesk.manga.impl.util.getChapterDownloadPath
import suwayomi.tachidesk.manga.impl.util.storage.FileDeletionHelper
import suwayomi.tachidesk.server.ApplicationDirs
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

private val applicationDirs by DI.global.instance<ApplicationDirs>()

/*
* Provides downloaded files when pages were downloaded into folders
* */
class FolderProvider(mangaId: Int, chapterId: Int) : ChaptersFilesProvider(mangaId, chapterId) {
    override fun getImageImpl(index: Int): Pair<InputStream, String> {
        val chapterDir = getChapterDownloadPath(mangaId, chapterId)
        val folder = File(chapterDir)
        folder.mkdirs()
        val file = folder.listFiles()?.sortedBy { it.name }?.get(index)
        val fileType = file!!.name.substringAfterLast(".")
        return Pair(FileInputStream(file).buffered(), "image/$fileType")
    }

    override fun extractExistingDownload() {
        // nothing to do
    }

    override suspend fun handleSuccessfulDownload() {
        val chapterDir = getChapterDownloadPath(mangaId, chapterId)
        val folder = File(chapterDir)

        val cacheChapterDir = getChapterCachePath(mangaId, chapterId)
        File(cacheChapterDir).copyRecursively(folder, true)
    }

    override fun delete(): Boolean {
        val chapterDirPath = getChapterDownloadPath(mangaId, chapterId)
        val chapterDir = File(chapterDirPath)
        if (!chapterDir.exists()) {
            return true
        }

        val chapterDirDeleted = chapterDir.deleteRecursively()
        FileDeletionHelper.cleanupParentFoldersFor(chapterDir, applicationDirs.mangaDownloadsRoot)
        return chapterDirDeleted
    }
}
