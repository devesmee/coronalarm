package com.example.coronalarm

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.coronalarm.database.DatabaseBuilder
import com.example.coronalarm.database.DatabaseHelperImpl
import com.example.coronalarm.database.HomeViewModel
import com.example.coronalarm.database.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment() {
    private var TAG:String = ".HomeFragment"

    private var currentSound = ""
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize view model and database.
        viewModel = ViewModelProvider(this,
            ViewModelFactory(
                DatabaseHelperImpl(DatabaseBuilder.getInstance((activity as MainActivity).application))))
            .get(HomeViewModel::class.java)

        // Get power value from db.
        viewModel.getPower().observe( viewLifecycleOwner, Observer {
            (activity as MainActivity).application.power = it
            Log.i(TAG, "getPower(): Power from db is ${(activity as MainActivity).application.power}")
            // Turn beacon on and off based on the power value.
            (activity as MainActivity).changeScanning()
            // Change the power button state based on the power value.
            view.powerButton.isActivated = (activity as MainActivity).application.power
        })

        // Get current sound from db.
        viewModel.getCurrentSound().observe(viewLifecycleOwner, Observer {
            currentSound = it
            viewModel.getSoundByName(currentSound).observe(viewLifecycleOwner, Observer {
                (activity as MainActivity).setNewSound(it)
            })
            Log.i(TAG, "viewModel.getCurrentSound().observe(): current sound is $currentSound")
            // Activate button to indicate which sound is currently used.
            when (currentSound) {
                view.buttonSound1.text.toString() -> selectedButton(view.buttonSound1, view)
                view.buttonSound2.text.toString() -> selectedButton(view.buttonSound2, view)
                view.buttonSound3.text.toString() -> selectedButton(view.buttonSound3, view)
                view.buttonSound4.text.toString() -> selectedButton(view.buttonSound4, view)
                view.buttonSound5.text.toString() -> selectedButton(view.buttonSound5, view)
            }
        })

        // Set button texts to favourited sounds.
        view.buttonSound1.text = (activity as MainActivity).favouritedSoundsArray[0].name
        view.buttonSound2.text = (activity as MainActivity).favouritedSoundsArray[1].name
        view.buttonSound3.text = (activity as MainActivity).favouritedSoundsArray[2].name
        view.buttonSound4.text = (activity as MainActivity).favouritedSoundsArray[3].name
        view.buttonSound5.text = (activity as MainActivity).favouritedSoundsArray[4].name

        /**************************************************/
        /**              Button click handlers           **/
        /**************************************************/
        view.powerButton.setOnClickListener {
            // Changes the state of the power button.
            (activity as MainActivity).application.power = !(activity as MainActivity).application.power
            powerButton.isActivated = (activity as MainActivity).application.power
            // Turn beacon on or off depending on the power button state.
            (activity as MainActivity).changeScanning()
            // Update power value in db.
            viewModel.setPower((activity as MainActivity).application.power)
        }

        view.buttonSound1.setOnClickListener {
            // Changes the current sound.
            viewModel.getSoundByName(view.buttonSound1.text.toString()).observe(this, Observer {
                (activity as MainActivity).setNewSound(it)
            })
            selectedButton(view.buttonSound1)
            viewModel.setCurrentSound(view.buttonSound1.text.toString())
        }

        view.buttonSound2.setOnClickListener {
            // Change the current sound.
            viewModel.getSoundByName(view.buttonSound2.text.toString()).observe(this, Observer {
                (activity as MainActivity).setNewSound(it)
            })
            selectedButton(view.buttonSound2)
            viewModel.setCurrentSound(view.buttonSound2.text.toString())
        }

        view.buttonSound3.setOnClickListener {
            // Change the current sound.
            viewModel.getSoundByName(view.buttonSound3.text.toString()).observe(this, Observer {
                (activity as MainActivity).setNewSound(it)
            })
            selectedButton(view.buttonSound3)
            viewModel.setCurrentSound(view.buttonSound3.text.toString())
        }

        view.buttonSound4.setOnClickListener {
            // Change the current sound.
            viewModel.getSoundByName(view.buttonSound4.text.toString()).observe(this, Observer {
                (activity as MainActivity).setNewSound(it)
            })
            selectedButton(view.buttonSound4)
            viewModel.setCurrentSound(view.buttonSound4.text.toString())
        }

        view.buttonSound5.setOnClickListener {
            // Change the current sound.
            viewModel.getSoundByName(view.buttonSound5.text.toString()).observe(this, Observer {
                (activity as MainActivity).setNewSound(it)
            })
            selectedButton(view.buttonSound5)
            viewModel.setCurrentSound(view.buttonSound5.text.toString())
        }
        /**************************************************/

        return view
    }

    /**************************************************/
    /** Functions to change selected button styling. **/
    /**************************************************/
    private fun selectedButton(button: Button, view: View){
        // Set all buttons to BOLD typefaces and selected button to BOLD_ITALIC.
        view.buttonSound1.setTypeface(null, Typeface.BOLD)
        view.buttonSound2.setTypeface(null, Typeface.BOLD)
        view.buttonSound3.setTypeface(null, Typeface.BOLD)
        view.buttonSound4.setTypeface(null, Typeface.BOLD)
        view.buttonSound5.setTypeface(null, Typeface.BOLD)
        button.setTypeface(null, Typeface.BOLD_ITALIC)

        // Activate selected button and deactivate the other ones.
        view.buttonSound1.isActivated = false
        view.buttonSound2.isActivated = false
        view.buttonSound3.isActivated = false
        view.buttonSound4.isActivated = false
        view.buttonSound5.isActivated = false
        button.isActivated = true
    }

     private fun selectedButton(button: Button){
        // Set all buttons to BOLD typefaces and selected button to BOLD_ITALIC.
        buttonSound1.setTypeface(null, Typeface.BOLD)
        buttonSound2.setTypeface(null, Typeface.BOLD)
        buttonSound3.setTypeface(null, Typeface.BOLD)
        buttonSound4.setTypeface(null, Typeface.BOLD)
        buttonSound5.setTypeface(null, Typeface.BOLD)
        button.setTypeface(null, Typeface.BOLD_ITALIC)

        // Activate selected button and deactivate the other ones.
        buttonSound1.isActivated = false
        buttonSound2.isActivated = false
        buttonSound3.isActivated = false
        buttonSound4.isActivated = false
        buttonSound5.isActivated = false
        button.isActivated = true
    }
    /**************************************************/



}