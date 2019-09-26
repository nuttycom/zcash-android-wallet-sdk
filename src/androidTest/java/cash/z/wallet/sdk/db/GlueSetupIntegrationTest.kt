package cash.z.wallet.sdk.db

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import cash.z.wallet.sdk.entity.CompactBlockEntity
import cash.z.wallet.sdk.jni.RustBackend
import cash.z.wallet.sdk.jni.RustBackendWelding
import cash.z.wallet.sdk.rpc.CompactTxStreamerGrpc
import cash.z.wallet.sdk.rpc.Service
import cash.z.wallet.sdk.rpc.Service.BlockID
import cash.z.wallet.sdk.rpc.Service.BlockRange
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.junit.AfterClass
import org.junit.Assert.assertNotNull
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

class GlueSetupIntegrationTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testDbExists() {
        assertNotNull(db)
//        Log.e("tezt", "addData")
//        addData()
//        Log.e("tezt", "scanData")
//        scanData()
//        Log.e("tezt", "checkResults")
//        checkResults()
    }

    private fun checkResults() {
        Thread.sleep(15000L)
    }

    private fun addData() {
        val result = blockingStub.getBlockRange(
            BlockRange.newBuilder()
                .setStart(BlockID.newBuilder().setHeight(373070L).build())
                .setEnd(BlockID.newBuilder().setHeight(373085L).build())
                .build()
        )
        while (result.hasNext()) {
            val compactBlock = result.next()
            dao.insert(CompactBlockEntity(compactBlock.height.toInt(), compactBlock.toByteArray()))
            System.err.println("stored block at height: ${compactBlock.height}")
        }
    }

    private fun scanData() {
        Log.e("tezt", "scanning blocks...")
        val result = rustBackend.scanBlocks()
        System.err.println("done.")
    }

    fun heightOf(height: Long): Service.BlockID {
        return BlockID.newBuilder().setHeight(height).build()
    }

    companion object {
        // jni
        val rustBackend: RustBackendWelding = RustBackend

        // db
        private lateinit var dao: CompactBlockDao
        private lateinit var db: CompactBlockDb
        private const val cacheDbName = "cache-glue.db"
        private const val dataDbName = "data-glue.db"

        // grpc
        lateinit var blockingStub: CompactTxStreamerGrpc.CompactTxStreamerBlockingStub

        @BeforeClass
        @JvmStatic
        fun setup() {
            rustBackend.create(ApplicationProvider.getApplicationContext(), cacheDbName, dataDbName)

            val channel = ManagedChannelBuilder.forAddress("10.0.2.2", 9067).usePlaintext().build()
            blockingStub = CompactTxStreamerGrpc.newBlockingStub(channel)

            db = Room
                .databaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    CompactBlockDb::class.java,
                    cacheDbName
                )
                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                .fallbackToDestructiveMigration()
                .build()
                .apply { dao = complactBlockDao() }
        }

        @AfterClass
        @JvmStatic
        fun close() {
            db.close()
            (blockingStub.channel as ManagedChannel).shutdown().awaitTermination(2000L, TimeUnit.MILLISECONDS)
        }
    }
}
