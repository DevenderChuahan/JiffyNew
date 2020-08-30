package `in`.jiffycharge.gopower.view.deposit

import `in`.jiffycharge.gopower.databinding.DepositSwipeLayoutBinding
import `in`.jiffycharge.gopower.model.ContentXX
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*


class DepositAdapter(val context: Context?, val list: List<ContentXX>) : RecyclerView.Adapter<DepositAdapter.Myviewholder>(){


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DepositAdapter.Myviewholder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = DepositSwipeLayoutBinding.inflate(inflater)
        return Myviewholder(binding)

//return Myviewholder(LayoutInflater.from(context).inflate(R.layout.orders_adapater_layout,parent,false))

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun onBindViewHolder(holder:Myviewholder, position: Int) {
        holder.bindItems(list.get(position))

    }

    override fun getItemCount(): Int {
        return list.size
//        return 10
    }

    class Myviewholder(val binding: DepositSwipeLayoutBinding): RecyclerView.ViewHolder(binding.root) {

        fun bindItems(content: ContentXX)
        {
            try {

//                val timestamp = content.createTime
//                val sdftime = SimpleDateFormat("h:mm a")
////                    val sdf = SimpleDateFormat("MM/dd/yyyy")
//                val sdf = SimpleDateFormat("MMM dd, yyyy")
//                val sdfDay = SimpleDateFormat("EEEE")
//                val netDate = Date(timestamp)
//                val date =sdf.format(netDate)
//                val time =sdftime.format(netDate)

                val pattern: String = "MM/dd/yy, HH:mm"
                val format = SimpleDateFormat(pattern, Locale.getDefault())
                val time=format.format( content.createTime).toString().trim()



//
                binding.tvType.text=content.type
                binding.tvTime.text=time
                binding.tvAmount.text= content.amount.toString().trim()
                binding.executePendingBindings()
//
//                binding.llOrdersView.setOnClickListener {
//                    if (it.context is HomeActivity)
//                    {
//                        (it.context as HomeActivity).open_ViewOrder(content.orderCode)
//
//                    }
//
//
//                }



            }catch (e:Exception)
            {
                e.printStackTrace()
                Log.v("DCException",e.toString())


            }

        }



    }

}