package com.example.indydemo.ident

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.indydemo.R


class AuthorisationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(com.twigbit.identsdk.R.layout.fragment_authorisation,
                container, false)
        val buttonContinue = view.findViewById<View>(R.id.buttonContinue) as Button

        buttonContinue.setOnClickListener {
            this.findNavController()
                    .navigate(R.id.action_authorisationFragment_to_accessRightsFragment)
        }

        return view
    }

}
