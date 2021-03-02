package `in`.jiffycharge.gopower.view.feedback

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.model.FeedbackVO
import `in`.jiffycharge.gopower.model.ItemXXXXXXXXXXXXX
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.view.map.MapFragment
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.github.dhaval2404.imagepicker.ImagePicker
import com.yzq.zxinglibrary.android.CaptureActivity
import com.yzq.zxinglibrary.bean.ZxingConfig
import com.yzq.zxinglibrary.common.Constant
import kotlinx.android.synthetic.main.activity_feedback.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class FeedbackActivity : AppCompatActivity(), PhotoAdapterClickListener {
    companion object {
        val feedbackvo = FeedbackVO()
        private const val FEEDBACK_TYPE_NORMAL = "1"
        private const val FEEDBACK_TYPE_ORDERING = "2"
        private const val FEEDBACK_TYPE_COMPELETE = "3"
        private const val REQUEST_CODE_SCAN = 100

    }

    private var allPhotoNumber = 0
    private var upSuccessPhotoNumber = 0
    private val homeViewmodel by viewModel<HomeActivityViewModel>()
    val home_view_model by viewModel<HomeActivityViewModel>()
    lateinit var context: Context
    lateinit var ordercode: String
    lateinit var borrowsyscode: String


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        context = this
        val intent = intent
        intent?.apply {
            if (this.hasExtra("ordercode")) {
                ordercode = this.getStringExtra("ordercode") ?: return@apply
                borrowsyscode = this.getStringExtra("borrowsyscode") ?: return@apply
                borrowsyscode.let {
                    powerBankCodeEt.setText(it)
                    powerBankCodeEt.isEnabled = false
                    scanCodeIv.isEnabled = false

                }
                ordercode.let {
                    feedbackvo.orderCode = ordercode
                }
                getfeedbacktitilelist(FEEDBACK_TYPE_ORDERING)
            } else {
                getfeedbacktitilelist(FEEDBACK_TYPE_NORMAL)

            }
        }
        rv_feedbackphoto.adapter = PhotoAdapter(context, this, homeViewmodel)
        PhotoAdapter.data.clear()
        setPhotoNumberHint(0)
        feedback_back.setOnClickListener {
            onBackPressed()
        }
        scanCodeIv.setOnClickListener {
            if (camera_permission()) {
                openCamera()


            }else {
                get_camera_permission()

            }
        }
        submitBt.setOnClickListener {

            allPhotoNumber = 0
            upSuccessPhotoNumber = 0
            PhotoAdapter.data.forEach {
                if (!it.isNullOrBlank()) {
                    allPhotoNumber++
                    startUpLoad(it)
                }
            }

        }

        //observe livedata
        home_view_model.repo.uploadFileresponse.observe(this, Observer {
            runOnUiThread {

                when (it.status) {
                    Resourse.Status.SUCCESS -> {
                        onSuccessUploadPhoto(it.data!!.item)
                    }
                    Resourse.Status.ERROR -> {
                        toast(it.exception?:return@runOnUiThread)

                    }


                    else -> {
                    }
                }

            }


        })
        home_view_model.repo.feedbackTitleListData.observe(this, Observer {

            runOnUiThread {

                when (it.status) {
                    Resourse.Status.SUCCESS -> {
                        pb_feedback.visibility = View.GONE
//                feedbackAdapter.refreshList(bean.data!!.items)
                        rv_feedback.adapter = FeedbackAdapter(this).apply {
                            it.data?.items?.let { it1 ->
                                this.updatelist(it1)
                                notifyDataSetChanged()
                            }


                        }

                    }
                    Resourse.Status.ERROR -> {

                        pb_feedback.visibility = View.GONE

                    }


                    else -> {
                        pb_feedback.visibility = View.GONE

                    }
                }
            }


        })
        homeViewmodel.repo.voResult.observe(this, Observer {
            runOnUiThread {

                when (it.status) {

                    Resourse.Status.SUCCESS -> {

                        toast("Uploaded Successfully !!")
                    }
                    Resourse.Status.ERROR -> {
                        toast(it.exception?:return@runOnUiThread)


                    }


                    else -> {
                    }
                }


            }


        })


    }

    private fun openCamera() {
        val intent = Intent(context, CaptureActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val config = ZxingConfig()
        config.reactColor = R.color.colorAccent
        config.isShowAlbum = false
        config.isShowFlashLight = false
        config.scanLineColor = R.color.colorAccent
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config)
        startActivityForResult(intent,REQUEST_CODE_SCAN)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun get_camera_permission() {
       requestPermissions(
            arrayOf<String>(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            REQUEST_CODE_SCAN
        )

    }


    fun camera_permission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_SCAN && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startUpLoad(filePath: String) {
        val file = File(filePath)
        val fileTypeDesc = "IMAGE"
        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        homeViewmodel.repo.uploadFile(fileTypeDesc, requestBody)
    }

    private fun onSuccessUploadPhoto(result: ItemXXXXXXXXXXXXX) {
        upSuccessPhotoNumber++
        //Due to frame defects (time to modify later), the order of pictures may change
        when (upSuccessPhotoNumber) {
            1 -> {
                feedbackvo.imgUrl1 = result.url

            }
            2 -> {
                feedbackvo.imgUrl2 = result.url

            }
            3 -> {
                feedbackvo.imgUrl3 = result.url

            }
            4 -> {
                feedbackvo.imgUrl4 = result.url

            }
        }
        //Picture uploaded successfully
        if (upSuccessPhotoNumber == allPhotoNumber) {
            homeViewmodel.repo.updatefeedback(FeedbackVO())

        }

    }

    private fun setPhotoNumberHint(currentNumber: Int) {
        tv_photoCount.text =
            resources.getString(R.string.upload_photos).plus("(${currentNumber}").plus("/")
                .plus("${PhotoAdapter.MAX_PHOTO})")

        feedbackvo.feedDesc = feedbackDesEt.text.toString()
        feedbackvo.sysCode = powerBankCodeEt.text.toString()

        submitBt.isEnabled = !feedbackvo.feedDesc.isNullOrBlank()
                && (PhotoAdapter(context, this, homeViewmodel).itemCount > 1)
                && !feedbackvo.sysCode.isNullOrBlank()
                && !feedbackvo.typeId.isNullOrBlank()

    }


    private fun getfeedbacktitilelist(feedbackType: String) {
        home_view_model.repo.findFeedbackTypeListUsingGET(feedbackType)
    }

    override fun photoclick(position: Int) {
        currentPhotoPosition = position
        ImagePicker.with(this)
            .crop()                    //Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .start(202)
//            .start()

    }

    private var currentPhotoPosition = 0

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == Activity.RESULT_OK && requestCode==202)
            {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data

                //You can get File object from intent
                val file: File = ImagePicker.getFile(data)!!

                //You can also get File Path from intent
                val filePath: String = ImagePicker.getFilePath(data)!!


                PhotoAdapter.data.add(currentPhotoPosition, filePath)
                home_view_model.repo.photoData.postValue(filePath)

                var photoNumber = 0
                PhotoAdapter.data.forEach {
                    if (!it.isNullOrBlank()) {


                        when (photoNumber) {
                            0 -> {
//                                    FeedbackVO (imgUrl1 = PhotoAdapter.data[photoNumber])
                                feedbackvo.imgUrl1 = PhotoAdapter.data[photoNumber]
                            }
                            1 -> {
//                                    FeedbackVO (imgUrl2 = PhotoAdapter.data[photoNumber])
                                feedbackvo.imgUrl2 = PhotoAdapter.data[photoNumber]


                            }
                            2 -> {
//                                    FeedbackVO (imgUrl3 = PhotoAdapter.data[photoNumber])
                                feedbackvo.imgUrl3 = PhotoAdapter.data[photoNumber]

                            }
                            3 -> {
//                                    FeedbackVO (imgUrl4 = PhotoAdapter.data[photoNumber])
                                feedbackvo.imgUrl4 = PhotoAdapter.data[photoNumber]

                            }
                            else -> {
                            }


                        }
                        photoNumber++

                    }
                }


                setPhotoNumberHint(photoNumber)


            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                toast(ImagePicker.getError(data))
            } else {
//            toast("Task Cancelled")

            }

            if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val content = data.getStringExtra(Constant.CODED_CONTENT)
//                    val content = data.getStringExtra("belongType")
                    Log.v("SCANDATA",data.toString())
                    powerBankCodeEt.setText(content)
                    powerBankCodeEt.setSelection(content.length)
                }
            }

        }catch (e:Exception)
        {

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animatoo.animateSwipeLeft(context)

    }

}