package `in`.jiffycharge.gopower.view.subscriptions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import `in`.jiffycharge.gopower.R

class SubscriptionAdapter(val context: Context, val list:HashMap<String,String>) :RecyclerView.Adapter<SubscriptionAdapter.Myviewholder>(){

    var count=0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubscriptionAdapter.Myviewholder {



return Myviewholder(LayoutInflater.from(context).inflate(R.layout.subscription_adapter_view,parent,false))

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: SubscriptionAdapter.Myviewholder, position: Int) {
            if(position%2==0)
            {
                holder.ll_top.background=ContextCompat.getDrawable(context,R.drawable.subscription_bacground_1)
            }else
            {
                holder.ll_top.background=ContextCompat.getDrawable(context,R.drawable.subscription_bacground_2)

            }




    }



    override fun getItemCount(): Int {
        return 30

    }


    class Myviewholder(itemview:View):RecyclerView.ViewHolder(itemview) {
//         val tv_top=itemview.findViewById<TextView>(R.id.tv_top)
         val ll_top=itemview.findViewById<LinearLayout>(R.id.ll_top)


    }

}