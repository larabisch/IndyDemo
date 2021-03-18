package com.example.indydemo.ident


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.indydemo.R


class IntroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(com.twigbit.identsdk.R.layout.fragment_intro,
                container, false)
        val buttonStart = view.findViewById<View>(R.id.buttonStart) as Button

        buttonStart.setOnClickListener {
            this.findNavController().navigate(R.id.action_introFragment_to_authorisationFragment)
        }

        return view
    }

}
