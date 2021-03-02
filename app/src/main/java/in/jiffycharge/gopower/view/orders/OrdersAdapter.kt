package `in`.jiffycharge.gopower.view.orders

import `in`.jiffycharge.gopower.databinding.OrdersAdapaterLayoutBinding
import `in`.jiffycharge.gopower.model.Content
import `in`.jiffycharge.gopower.model.Order_list_model
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.view.home.HomeActivity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*


class OrdersAdapter(val context: Context, val list: Resourse<Order_list_model>) :RecyclerView.Adapter<OrdersAdapter.Myviewholder>(){


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrdersAdapter.Myviewholder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = OrdersAdapaterLayoutBinding.inflate(inflater)
        return Myviewholder(binding)

//return Myviewholder(LayoutInflater.from(context).inflate(R.layout.orders_adapater_layout,parent,false))

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
//    override fun getItemViewType(position: Int): Int {
//        return position
//    }


    override fun onBindViewHolder(holder:Myviewholder, position: Int) {

        holder.bindItems(list.data!!.content.get(position))

    }



    override fun getItemCount(): Int {
        return list.data!!.content.size
//        return 1

    }


    class Myviewholder(val binding:OrdersAdapaterLayoutBinding):RecyclerView.ViewHolder(binding.root) {

        fun bindItems(content: Content)
        {


            try {

                val timestamp = content.payTime

                val sdftime = SimpleDateFormat("h:mm a")
//                    val sdf = SimpleDateFormat("MM/dd/yyyy")
                val sdf = SimpleDateFormat("MMM dd, yyyy")
                val sdfDay = SimpleDateFormat("EEEE")
                val netDate = Date(timestamp)
                val date =sdf.format(netDate)
                val time =sdftime.format(netDate)



                binding.tvDate.text=date.toString().trim()
                binding.tvTitle1.text=content.beginLocationDetails
                binding.tvTitle2.text=content.endLocaitonDetails
                binding.tvAmount.text=content.price.toString().trim()
                binding.tvTime.text=time.toString().trim()


                binding.executePendingBindings()



                binding.llOrdersView.setOnClickListener {
                    if (it.context is HomeActivity)
                    {
                        (it.context as HomeActivity).open_ViewOrder(content.orderCode)

                    }


                }




            }catch (e:Exception)
            {
                e.printStackTrace()
                Log.v("DCException",e.toString())


            }

        }



//         val tv_top=itemview.findViewById<TextView>(R.id.tv_top)
//         val ll_orders_view=itemview.findViewById<LinearLayout>(R.id.ll_orders_view)


    }

}