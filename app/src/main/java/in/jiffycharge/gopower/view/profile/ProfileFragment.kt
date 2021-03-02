package `in`.jiffycharge.gopower.view.profile
import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.databinding.FragmentProfileBinding
import `in`.jiffycharge.gopower.model.ItemXXXXXXXXXXXXX
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.view.home.HomeActivity
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {
    lateinit var context: Activity
    private var isUpload = true

    private val home_view_model by viewModel<HomeActivityViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context = requireContext() as Activity

        val bind = DataBindingUtil.inflate<FragmentProfileBinding>(
            inflater,
            R.layout.fragment_profile,
            container,
            false
        )
            .apply {
                this.setLifecycleOwner(this@ProfileFragment)
                this.profielVmodel = home_view_model
            }

        return bind.root

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()



        ll_edit_profile.setOnClickListener {
//            selectPhoto()

            checkPermissionAndStartGallery()


        }


        profile_back.setOnClickListener {
            context.onBackPressed()
        }

    }

    private fun checkPermissionAndStartGallery() {

        Dexter.withContext(context)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {

                        Matisse.from(this@ProfileFragment)
                            .choose(MimeType.ofImage())
                            .countable(true)
//                            .gridExpectedSize(120)
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(GlideEngine())
                            .forResult(101)
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permenantly, navigate user to app settings

                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri =
                            Uri.fromParts("package", context.packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {

                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()

    }


    private fun initObserver() {
//                home_view_model.repo.fetchUserprofile()
        home_view_model.repo.uploadFileresponse.observe(viewLifecycleOwner, Observer {
            context.runOnUiThread {

                when (it.status) {
                    Resourse.Status.SUCCESS -> {
                        onSuccessUploadPhoto(it.data!!.item)
                    }
                    Resourse.Status.ERROR -> {
                        onFailUploadPhoto(it.data!!.error_description)
                        pic_loader.visibility = View.GONE


                    }


                    else -> {
                    }
                }

            }


        })
        home_view_model.repo._data.observe(viewLifecycleOwner, Observer { profile_data ->
            context.runOnUiThread {
                when (profile_data.status) {

                    Resourse.Status.SUCCESS -> {


                        tv_name.setText(profile_data.data?.item?.nickname)
                        tv_email.setText("")
                        tv_cont_no.setText(profile_data.data?.item?.mobile)

                        if (profile_data.data?.item?.isVIP == true) {
                            ll_gold_user.visibility = View.VISIBLE
                        } else {
                            ll_gold_user.visibility = View.GONE

                        }
                        if (!profile_data.data?.item?.headImgPath.isNullOrBlank()) {
                            img_profile_pic.setImageURI(null)

//                                Picasso.with(context).load(Uri.parse(profile_data.item.headImgPath)).into(img_profile_pic)
                            Glide.with(this).load(profile_data.data?.item?.headImgPath).apply(
                                RequestOptions.bitmapTransform(CircleCrop()).diskCacheStrategy(
                                    DiskCacheStrategy.ALL
                                )
                            ).listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {

//                                                img_profile_pic.

                                    return false


                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false

                                }


                            })

                                .into(img_profile_pic)


                        }
                    }
                    Resourse.Status.ERROR -> {
                        tv_name.setText("N/A")
                        tv_cont_no.setText("N/A")
                        img_profile_pic.setImageURI(null)
                        ll_gold_user.visibility = View.GONE


                    }


                }


            }
        })
        home_view_model.repo.headeruploadFileresponse.observe(viewLifecycleOwner, Observer {
            context.runOnUiThread {
                when (it.status) {
                    Resourse.Status.SUCCESS -> {

                        home_view_model.repo.fetchUserprofile()
                        pic_loader.visibility = View.GONE
                        EventBus.getDefault().post("photoUploded")


                    }
                    Resourse.Status.ERROR -> {
                        pic_loader.visibility = View.GONE


                    }


                    else -> {
                    }
                }

            }


        })
    }

    private fun onFailUploadPhoto(errorMsg: Any) {
        context.toast(errorMsg.toString())
        pic_loader.visibility = View.GONE


    }

    private fun onSuccessUploadPhoto(item: ItemXXXXXXXXXXXXX) {
        home_view_model.repo.updateUserHeadImage(item.url)


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                val mSelected = Matisse.obtainPathResult(data) as ArrayList<String>
                Log.e("imageurl",mSelected[0])

                startUpLoad(mSelected[0])


            }


        }

    }


    private fun onTakeSuccess(imageUrl: String?) {
        pic_loader.visibility = View.GONE


    }

    fun startUpLoad(imageUrl: String) {
        pic_loader.visibility = View.VISIBLE

        val file = File(imageUrl)
        val fileTypeDesc = "IMAGE"
        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        home_view_model.repo.uploadFile(fileTypeDesc, requestBody)

    }


}


