package `in`.jiffycharge.gopower.view.feedback

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.databinding.FeedbacklistlayyoutBinding
import `in`.jiffycharge.gopower.model.FeedbackVO
import `in`.jiffycharge.gopower.model.ItemXXXXXXXXXXXXXX
import `in`.jiffycharge.gopower.view.feedback.FeedbackActivity.Companion.feedbackvo
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class FeedbackAdapter(val context:Context): RecyclerView.Adapter<MyFeedbakViewholder>() {
    var data: List<ItemXXXXXXXXXXXXXX>?=null
private  var lastSelectedPosition=-1

    fun updatelist(items: List<ItemXXXXXXXXXXXXXX>)
    {
        this.data=items

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFeedbakViewholder {
        val inflater=LayoutInflater.from(context)
        val binding=FeedbacklistlayyoutBinding.inflate(inflater)
        return MyFeedbakViewholder(binding)


    }

    override fun getItemCount(): Int {
        return data?.size?:0
    }

    override fun onBindViewHolder(holder: MyFeedbakViewholder, position: Int) {
        holder.binditems(data!!.get(position))
        holder.binding.llFeedbackView.setOnClickListener {
            lastSelectedPosition=position

            feedbackvo.typeId= data!!.get(position).id.toString()
            notifyDataSetChanged()

        }
        if (lastSelectedPosition==position)
        {
            holder.binding.llChangebackground.setBackgroundColor( ContextCompat.getColor(context, R.color.colorPrimaryDark))
        }else
        {
            holder.binding.llChangebackground.setBackgroundColor( ContextCompat.getColor(context, R.color.transparent))


        }

    }

}

class MyFeedbakViewholder(val binding:FeedbacklistlayyoutBinding):RecyclerView.ViewHolder(binding.root) {
    fun binditems(content: ItemXXXXXXXXXXXXXX)
    {

        binding.tvIssue.text=content.typeName


    }

}
