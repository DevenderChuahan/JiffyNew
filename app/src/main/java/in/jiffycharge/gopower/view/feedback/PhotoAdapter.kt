package `in`.jiffycharge.gopower.view.feedback


import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.databinding.PhotoadadpterlayoutBinding
import `in`.jiffycharge.gopower.model.FeedbackVO
import `in`.jiffycharge.gopower.view.feedback.PhotoAdapter.Companion.data
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PhotoAdapter(
    val context: Context,
    val photoAdapterClickListener: PhotoAdapterClickListener,
   val  homeViewmodel: HomeActivityViewModel
): RecyclerView.Adapter<MyPhotoViewholder>() {

    var currentposition=0

    companion object{
        const val MAX_PHOTO=4
        var flag=false


        var  data=ArrayList<String>()

//            get() = imagelist

    }
//    val feedbackBean = FeedbackVO()







    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPhotoViewholder {
        val inflater=LayoutInflater.from(context)
        val binding=PhotoadadpterlayoutBinding.inflate(inflater)
        return MyPhotoViewholder(binding)


    }

    override fun getItemCount(): Int {


        if (!data.isNullOrEmpty())
        {
            if (currentposition < MAX_PHOTO)
            {
                return data.size+1

            }else
            {
                return MAX_PHOTO
            }



        }else
        {
            return 1

        }
    }




    override fun onBindViewHolder(holder: MyPhotoViewholder, position: Int) {

        currentposition=position

//            holder.binditems(data.get(position),position,feedbackBean,context)

            homeViewmodel.repo.photoData.observeForever {
                if (it!=null) {
                    if (position < MAX_PHOTO)
                    {

                        holder.binditems(it, position, FeedbackVO(), context,homeViewmodel)
                }



//                    if (data.size>1)
//                    {
//                        holder.binditems(data.get(1), position, feedbackBean, context)
//
//                    }else {
//                        holder.binditems(data.get(0), position, feedbackBean, context)
//                    }


                }



            }









            holder.binding.llPhotoview.setOnClickListener {
                if (position<MAX_PHOTO) {
                    photoAdapterClickListener.photoclick(position)
                }




        }


    }

}



class MyPhotoViewholder(val binding:PhotoadadpterlayoutBinding):RecyclerView.ViewHolder(binding.root) {
   public fun binditems(
       imgPath: String,
       position: Int,
       feedbackBean: FeedbackVO,
       context: Context,
       homeViewmodel: HomeActivityViewModel
   )
    {
        Log.v("datasize", "bind cliked")

        if (imgPath.isNullOrBlank() || position==data.size) {
            binding.photoIv.setImageResource(R.drawable.group_2)
            binding.llPhotoview.isEnabled=true
            binding.llPhotoview.isClickable=true
        } else {


            Glide.with(context).load(data.get(position)).into(binding.photoIv)
            binding.llPhotoview.isEnabled=false
            binding.llPhotoview.isClickable=false


        }






    }



}


