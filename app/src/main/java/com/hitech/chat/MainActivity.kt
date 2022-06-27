package com.hitech.chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.hitech.chat.databinding.ActivityMainBinding
import com.hitech.chats.HaiChatEngine
import com.hitech.chats.chat.HaiChatListenerImpl
import com.tencent.rtmp.ui.TXCloudVideoView

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    private lateinit var mainBinding: ActivityMainBinding

    private lateinit var localView: TXCloudVideoView
    private lateinit var remoteView: TXCloudVideoView
    private lateinit var exitRoomBtn: AppCompatButton

    private val haiChatListener = object : HaiChatListenerImpl() {

        override fun onEnterRoom(result: Long) {
            Log.d(TAG, "Time to enter the room: $result")
            Toast.makeText(this@MainActivity, "Time to enter the room: $result", Toast.LENGTH_LONG)
                .show()
        }

        override fun onExitRoom(result: Int) {
            Log.d(TAG, "exit room result: $result")
            Toast.makeText(this@MainActivity, "exit the room: $result", Toast.LENGTH_LONG)
                .show()
        }

        override fun onRemoteUserEnterRoom(userId: String?) {
            Log.d(TAG, "onRemoteUserEnterRoom userId: $userId")
        }

        override fun onRemoteUserLeaveRoom(userId: String?, reason: Int) {
            Log.d(TAG, "onRemoteUserLeaveRoom userId : $userId ,reason: $reason")
            HaiChatEngine.exitRoom()
            this@MainActivity.finish()
        }

        override fun onUserVideoAvailable(userId: String?, available: Boolean) {
            Log.d(TAG, "onUserVideoAvailable userId: $userId ,available: $available")
            HaiChatEngine.refreshRemoteVideoView(userId, remoteView, available)
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isPreview = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        HaiChatEngine.addVideoRoomListener(haiChatListener)

        localView = mainBinding.localView
        remoteView = mainBinding.remoteView
        exitRoomBtn = mainBinding.testExitRoom

        handler.postDelayed({
            HaiChatEngine.enterRoom(localView)
        }, 300)

        exitRoomBtn.setOnClickListener {
            HaiChatEngine.exitRoom()
            finish()
        }

        mainBinding.testExitRoom.setOnClickListener {
            //isPreview true 开启摄像头 ，false 关闭摄像头
            HaiChatEngine.localPreviewSwitch(isPreview, localView)
            isPreview =!isPreview
        }

    }
}