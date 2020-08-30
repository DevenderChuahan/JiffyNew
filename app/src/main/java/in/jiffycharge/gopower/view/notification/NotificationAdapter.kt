package `in`.jiffycharge.gopower.view.notification

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.jiffycharge.gopower.R

class NotificationAdapter(val context: Context,val list:HashMap<String,String>) :RecyclerView.Adapter<NotificationAdapter.Myviewholder>(){

    var count=0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapter.Myviewholder {



return Myviewholder(LayoutInflater.from(context).inflate(R.layout.notification_adapter_view,parent,false))

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: NotificationAdapter.Myviewholder, position: Int) {
        count++
        if(position==1)
        {
            holder.tv_top.setTextColor(ColorStateList.valueOf(Color.parseColor("#fc258f")))

        }else if(count==2)
        {
            holder.tv_top.setTextColor(ColorStateList.valueOf(Color.parseColor("#25fcc7")))

        }
        else if(count==3)
        {
            holder.tv_top.setTextColor(ColorStateList.valueOf(Color.parseColor("#25d0fc")))
            count=0

        }



    }



    override fun getItemCount(): Int {
        return 50

    }


    class Myviewholder(itemview:View):RecyclerView.ViewHolder(itemview) {
         val tv_top=itemview.findViewById<TextView>(R.id.tv_top)


    }

}