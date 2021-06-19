package com.aya.receiver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.aya.receiver.databinding.ActivityMainBinding
import java.io.*
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

class MainActivity : AppCompatActivity() {

    lateinit var serverSocket: ServerSocket
    lateinit var clientSocket_main: Socket
    lateinit var serverThread:Thread
    lateinit var clientThread:ClientThread
    lateinit var handler: Handler
    val SERVER_PORT : Int = 3004
    final val   SERVER_IP :String = "10.1.10.113"

    lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this,  R.layout.activity_main)


        activityMainBinding.connection.setOnClickListener { view ->
            Toast.makeText(this,"Reciver"+"Connecting to server ........", Toast.LENGTH_SHORT).show()
            clientThread = ClientThread(activityMainBinding)
            serverThread = Thread(clientThread)
            serverThread.start()
            Toast.makeText(this,"Reciver"+"Connected to server", Toast.LENGTH_SHORT).show()
            activityMainBinding.connection.visibility = View.GONE
            activityMainBinding.sendData.visibility = View.VISIBLE
        }

        activityMainBinding.sendData.setOnClickListener { view ->


        }
    }

    fun showMessage(message : String ){
        handler.post{ Runnable {
            kotlin.run {
                activityMainBinding.data.text = message + activityMainBinding.data.text
            } } }
    }


    class ClientThread (activityMainBinding: ActivityMainBinding): Runnable{
        lateinit var scoket: Socket
        lateinit var input : BufferedReader
        final val   SERVER_IP :String = "10.1.10.113"
        val SERVER_PORT : Int = 3004
        val activityMainBinding: ActivityMainBinding = activityMainBinding
        override fun run() {

            val inputAdress: InetAddress = InetAddress.getByName(SERVER_IP)
            scoket = Socket(inputAdress,SERVER_PORT)

            while (! Thread.currentThread().isInterrupted){
                input = BufferedReader(InputStreamReader(scoket.getInputStream()))
                var message = "Reciver"+input.readLine()

                if(message == null || message.contentEquals("Disconnect")){
                    Thread.interrupted()
                    message = "Reciver"+"Server Disconnect"
                    break
                }
                activityMainBinding.data.text = message
            }


        }
        fun sendData(data:String) {
            if(scoket != null){
                Thread(Runnable {
                    kotlin.run {

                        var printWriter: PrintWriter
                        try {
                            printWriter = PrintWriter(
                                    BufferedWriter(
                                            OutputStreamWriter(scoket.getOutputStream())), true)
                            printWriter.println(data)
                        }catch (e: IOException){
                        }
                    }
                }).start()

            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        if(clientThread != null){
           clientThread.sendData("Disconnect")
            clientThread = null!!
        }
    }
}