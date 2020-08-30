package `in`.jiffycharge.gopower.view.profile


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.databinding.FragmentOrderBinding
import `in`.jiffycharge.gopower.databinding.FragmentProfileBinding
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import `in`.jiffycharge.gopower.viewmodel.Orders_view_model
import android.app.Activity
import android.net.Uri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.nav_header_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    lateinit var context: Activity
    private val home_view_model by viewModel<HomeActivityViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context = requireContext() as Activity

        val bind= DataBindingUtil.inflate<FragmentProfileBinding>(inflater,R.layout.fragment_profile,container,false)
            .apply {
                this.setLifecycleOwner(this@ProfileFragment)
                this.profielVmodel = home_view_model
            }

        return  bind.root

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        home_view_model.repo.fetchUserprofile()



                    home_view_model.repo._data.observe(viewLifecycleOwner, Observer {profile_data->
                        context.runOnUiThread {
                            when(profile_data.status)
                            {

                                Resourse.Status.SUCCESS->
                                {
                                    tv_name.setText(profile_data.data!!.item.nickname)
                                    tv_email.setText("")
                                    tv_cont_no.setText(profile_data.data.item.mobile)
                                    img_profile_pic.setImageURI(null)

                                    if(profile_data.data.item.isVIP)
                                    {
                                        ll_gold_user.visibility=View.VISIBLE
                                    }else
                                    {
                                        ll_gold_user.visibility=View.GONE

                                    }
                                    if(!profile_data.data.item.headImgPath.isNullOrBlank())
                                    {
//                                Picasso.with(context).load(Uri.parse(profile_data.item.headImgPath)).into(img_profile_pic)
                                        Glide.with(this).load(profile_data.data.item.headImgPath).apply(
                                            RequestOptions.bitmapTransform(CircleCrop()).diskCacheStrategy(
                                                DiskCacheStrategy.ALL)
                                        ).into(img_profile_pic)


                                    }
                                }
                                Resourse.Status.ERROR->
                                {
                                    tv_name.setText("N/A")
                                    tv_cont_no.setText("N/A")
                                    img_profile_pic.setImageURI(null)
                                    ll_gold_user.visibility=View.GONE


                                }



                            }




                        }
                    })





//
//        if(FirebaseAuth.getInstance().currentUser!=null)
//        {
//
////            tv_name.setText("")
////            tv_email.setText("")
////            tv_cont_no.setText(FirebaseAuth.getInstance().currentUser!!.phoneNumber)
////            img_profile_pic.setImageURI(null)
//
//
//
//        }

        profile_back.setOnClickListener {
            context.onBackPressed()
        }

    }

}


