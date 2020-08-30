package `in`.jiffycharge.gopower.view.coupons

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import `in`.jiffycharge.gopower.R

class CouponsAdapter(val context: Context, val list:HashMap<String,String>) :RecyclerView.Adapter<CouponsAdapter.Myviewholder>(){

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





    }



    override fun getItemCount(): Int {
        return 10

    }


    class Myviewholder(itemview:View):RecyclerView.ViewHolder(itemview) {
//         val tv_top=itemview.findViewById<TextView>(R.id.tv_top)
//         val ll_orders_view=itemview.findViewById<LinearLayout>(R.id.ll_orders_view)


    }

}