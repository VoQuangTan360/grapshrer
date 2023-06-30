package com.example.nearmekotlindemo.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.nearmekotlindemo.Post
import com.example.nearmekotlindemo.R
import com.example.nearmekotlindemo.databinding.FragmentCreatePostBinding
import com.example.nearmekotlindemo.databinding.FragmentDialogChooseTypeCreatepostBinding
import com.example.nearmekotlindemo.databinding.FragmentDialogFeedbackBinding
import com.example.nearmekotlindemo.databinding.FragmentDialogmessBinding
import com.example.nearmekotlindemo.models.googlePlaceModel.GooglePlaceModel
import com.example.nearmekotlindemo.models.googlePlaceModel.Mess
import com.example.nearmekotlindemo.models.googlePlaceModel.Star
import com.example.nearmekotlindemo.models.googlePlaceModel.StatusID
import com.example.nearmekotlindemo.utility.LoadingDialog
import com.example.nearmekotlindemo.utility.State
import com.example.nearmekotlindemo.viewModels.PostViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.math.RoundingMode
import java.text.DecimalFormat


class DialogFeeback : DialogFragment() {
    lateinit var model: PostViewModel
    private val postViewModel: PostViewModel by viewModels()
    var gmail:String=""
     lateinit var binding: com.example.nearmekotlindemo.databinding.FragmentDialogFeedbackBinding
//    var data = MutableLiveData<Post>()

    private lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDialogFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model1 = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)
        val databasUser = Firebase.database.getReference("Star")
        model1.postMess.observe(viewLifecycleOwner,Observer{
            model1.getRequseStar(it.gmail)
           binding.textMess.text=it.mess
           binding.txtvehicle.text=it.vehicle
           binding.txtnameRequet.text=it.name
           binding.textTimeStartF.text=it.time
            gmail=it.gmail
            Glide.with(requireContext()).load(it.image)
                .into(binding.imgProfileRequest)

//           binding.textMess.text=it.
            Log.d(TAG,"check StatusID(it.id,it.idPost): "+it)
            model1.setidRequest(StatusID(it.id,it.postId))
            databasUser.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(TAG,"kiem tra onDataChange Star  : "+snapshot.value)
                    try {

                        var  userPlaces : List<Star> = snapshot.children.map { dataSnapshot ->

                            dataSnapshot.getValue(Star::class.java)!!

                        }
                        for(i in userPlaces){
                            if(i.gmail==it.gmail){
                                Log.d(TAG,"kiem tra onDataChange Star item  : "+i)

                                val df = DecimalFormat("#.##")
                                df.roundingMode = RoundingMode.CEILING

                                binding.txtPlaceDRating.text=df.format(i.poit)
                                binding.txtPlaceDRatingCount.text="("+i.vote+")"
                            }
                        }
                    }catch (e : Exception){
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })
        })


        model1.star.observe(viewLifecycleOwner, Observer {
            Log.d(TAG,"check Star: "+it)
            binding.txtPlaceDRating.text=it.poit.toString()
            binding.txtPlaceDRatingCount.text="("+it.vote+")"
        })

        binding.btnYes.setOnClickListener{
            val firebase = Firebase.database.getReference("CheckFollow")
            model1.idRequest.observe(viewLifecycleOwner,Observer{
                postViewModel.updateStatusPost(it.status,"2")
                postViewModel.updateStatusRequest(it.id,"2")
            })

            model1.idPost.observe(viewLifecycleOwner,Observer{
                postViewModel.getRequseMess(StatusID(it.id,"2") )
                firebase.child(it.id).setValue(StatusID(it.id,"1"))
            })
            dismiss()
        }
        binding.btnDeny.setOnClickListener {
            model1.idRequest.observe(viewLifecycleOwner,Observer{
//                postViewModel.updateStatusPost(it.status,"0")
                postViewModel.updateStatusRequest(it.id,"0")
            })
            dismiss()
        }
        binding.btnCance.setOnClickListener {
            dismiss()
        }
    }



}