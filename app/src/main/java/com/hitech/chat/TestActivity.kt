package com.hitech.chat

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hitech.chat.databinding.ActivityTestBinding
import com.hitech.chats.HaiChatEngine
import com.hitech.chats.entry.AskCallParams
import com.hitech.chats.network.CallListener
import com.hitech.chats.repository.ResponeWrapper
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class TestActivity : AppCompatActivity() {
    val TAG = "TestActivity"
    private lateinit var testActivityBinding: ActivityTestBinding

    private val callListener = object : CallListener() {
        override fun acceptCall(roomId: String?, id: String?) {
            // The anchor has agreed to call
            Log.d(TAG, "acceptCall roomId: $roomId ,id: $id")
            handler.post {
                startActivity(Intent(this@TestActivity, MainActivity::class.java))
            }
        }

        override fun rejectCall(roomId: String?, id: String?) {
            // The anchor has rejected to call
            Log.d(TAG, "rejectCall roomId: $roomId ,id: $id")
            handler.post {
                Toast.makeText(this@TestActivity, "rejectCall", Toast.LENGTH_LONG).show()
            }
        }

        override fun dialTimeout(roomId: String?, id: String?) {
            // The call is timeout
            Log.d(TAG, "dialTimeout roomId: $roomId ,id: $id")
            handler.post {
                Toast.makeText(this@TestActivity, "dialTimeout", Toast.LENGTH_LONG).show()
            }
        }

        override fun onError(code: Int, message: String?) {
            Log.d(TAG, "onError code: $code message: $message")
            handler.post {
                Toast.makeText(this@TestActivity, "onError", Toast.LENGTH_LONG).show()
            }
        }
    }

    val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        testActivityBinding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(testActivityBinding.root)


        HaiChatEngine.initSdk(application, true)

        HaiChatEngine.addCallListener(callListener)

        testActivityBinding.testCancelInvite.setOnClickListener {
            HaiChatEngine.cancelCallInvitation()
        }
        testActivityBinding.testCreatInvite.setOnClickListener {
            HaiChatEngine.creatCallInvitation()
        }
        val inputId = testActivityBinding.testId
        val inputUserId = testActivityBinding.testUserId
        testActivityBinding.testCall.setOnClickListener {
            val txt = inputId.text.toString()
            val userTxt = inputUserId.text.toString()

            if (userTxt.isBlank()) {
                Toast.makeText(this@TestActivity, "用户ID不能为空！", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (txt.isBlank()) {
                Toast.makeText(this@TestActivity, "主播ID不能为空！", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val toInt = txt.toInt()

            val askCallParams = AskCallParams.Builder()
                .setAskAvatar("https://variety.com/wp-content/uploads/2022/04/Screen-Shot-2022-04-04-at-2.18.37-PM.png")
                .setAskNickName("ptsd")
                .setAskId(userTxt)
                .setAnswerId(toInt)
                .build()
            HaiChatEngine.creatCallParams(askCallParams, object : ResponeWrapper<String> {
                override fun onComplete(d: String) {
                    Toast.makeText(this@TestActivity, "success: $d", Toast.LENGTH_LONG).show()
                }

                override fun onFailure(code: Int, msg: String?) {
                    // code: 6001 主播忙
                    Toast.makeText(this@TestActivity, "failed: $msg", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    @NeedsPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun requestPermission() {

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @OnPermissionDenied(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun deniedPermission() {

    }
}