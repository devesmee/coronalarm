package com.example.coronalarm

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.coronalarm.database.*
import kotlinx.android.synthetic.main.fragment_sounds.view.*
import java.io.File
import java.io.IOException

class SoundsFragment : Fragment() {
    private lateinit var viewModel: HomeViewModel
    var dialogBuilder: AlertDialog.Builder? = null
    private var recorder: MediaRecorder? = null
    var adapter: SoundAdapter? = null
    lateinit var mp: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogBuilder = AlertDialog.Builder(activity as MainActivity)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_sounds, container, false)

        // Initialize view model and database.
        viewModel = ViewModelProvider(this,
            ViewModelFactory(
                DatabaseHelperImpl(DatabaseBuilder.getInstance((activity as MainActivity).application))
            )
        )
            .get(HomeViewModel::class.java)

        // Get all the sounds from the database
        viewModel.getAllSounds().observe(this, Observer {
            val listViewSounds: ListView = view.findViewById(R.id.list_sounds) as ListView
            (activity as MainActivity).soundsArray = it
            adapter = SoundAdapter(activity as MainActivity, it)
            listViewSounds.setAdapter(adapter)
            listViewSounds.setOnItemClickListener(OnItemClickListener { arg0, arg1, position, arg3 ->
                var clickedSound = (activity as MainActivity).soundsArray[position]
                clickedSound.isFavourited = !clickedSound.isFavourited
                // Set the sound to favourited/not-favourited in database
                viewModel.setFavourited(clickedSound.isFavourited, clickedSound.name)
                // to get the path of the file
                val completePath = Environment.getExternalStorageDirectory()
                    .toString() + "/" + clickedSound.fileName
                mp = MediaPlayer.create((activity as MainActivity), Uri.parse(completePath))
                mp.start()
                adapter!!.notifyDataSetChanged()
            })
        })

        // To record a new sound
        view.recordButton.setOnTouchListener(OnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.recordButton.isPressed = true
                    startRecording()
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    view.recordButton.isPressed = false
                    stopRecording()
                }
            }
            false
        })

        return view
    }

    /**************************************************/
    /**                Recording Actions             **/
    /**************************************************/
    private fun startRecording() {
        // Function to start recording.
        recorder = MediaRecorder()
        recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        try {
            recorder?.setOutputFile(
                Environment.getExternalStorageDirectory()
                    .absolutePath + "/temp"
            )
            recorder?.prepare()
            recorder?.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        // Function to stop recording.
        if(null != recorder){
            recorder?.stop()
            recorder?.reset()
            recorder?.release()
            //DIALOG
            val inputRecordingName = EditText(activity as MainActivity)
            dialogBuilder!!.setView(inputRecordingName)
            dialogBuilder!!.setMessage("What is the name of your recording ?")
                .setCancelable(false)
                .setPositiveButton(
                    "Save recording"
                ) { dialog, id ->
                    val newSound = Sound(inputRecordingName.text.toString(), false, inputRecordingName.text.toString())
                    (activity as MainActivity).soundsArray.add(newSound)
                    // Rename temp file to input text
                    val tempFile: File = File(
                        Environment.getExternalStorageDirectory()
                            .getAbsolutePath(), "temp"
                    )
                    val recordedFile: File = File(
                        Environment.getExternalStorageDirectory()
                            .getAbsolutePath(), inputRecordingName.text.toString()
                    )
                    tempFile.renameTo(recordedFile)
                    // Add the sound to the database
                    viewModel.addSound(newSound)
                    viewModel.getAllSounds().observe(this, Observer {
                        adapter?.notifyDataSetChanged()
                    })
                    dialog.dismiss()
                }
                .setNegativeButton(
                    "Cancel"
                ) { dialog, id -> //  Action for 'NO' Button
                    dialog.dismiss()
                }
            //Creating dialog box
            val alert = dialogBuilder!!.create()
            //Setting the title
            alert.setTitle("Save recording")
            alert.show()

            recorder = null;
        }
    }
}