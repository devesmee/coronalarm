package com.example.coronalarm

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.coronalarm.database.Sound


class SoundAdapter(private val context: Context,
                    private val dataSource: ArrayList<Sound>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.list_item_sound, parent, false)

        // Get name sound
        val nameSoundTextView = rowView.findViewById(R.id.text_list_item) as TextView

        // Get subtitle element
        val favSoundImageButton = rowView.findViewById(R.id.icon_list_item) as ImageButton

        val sound = getItem(position) as Sound

        nameSoundTextView.text = sound.name
        favSoundImageButton.isSelected = sound.isFavourited


        return rowView
    }

}