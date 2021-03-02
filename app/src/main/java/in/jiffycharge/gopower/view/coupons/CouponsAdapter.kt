package `in`.jiffycharge.gopower.view.coupons

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.model.ContentXXX
import android.util.Log
import android.widget.TextView
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class CouponsAdapter(val context: Context, val list: ArrayList<ContentXXX>) :RecyclerView.Adapter<CouponsAdapter.Myviewholder>(){

    var count=0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CouponsAdapter.Myviewholder {



return Myviewholder(LayoutInflater.from(context).inflate(R.layout.coupons_adapter_view,parent,false))

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: CouponsAdapter.Myviewholder, position: Int) {

        holder.tv_off.text to list.get(position).amount.toString()+"%"+" off"

        val sdf = SimpleDateFormat("dd MMMM, yyyy hh:mm a")
        val date =sdf.format(list.get(position).expireDate )
        Log.e("CouponDate",date)

        holder.tv_date.text to "Expires on: "+date


    }



    override fun getItemCount(): Int {
        return list.size

    }


    class Myviewholder(itemview:View):RecyclerView.ViewHolder(itemview) {
         val tv_off=itemview.findViewById<TextView>(R.id.tv_off)
         val tv_date=itemview.findViewById<TextView>(R.id.tv_date)


    }

}