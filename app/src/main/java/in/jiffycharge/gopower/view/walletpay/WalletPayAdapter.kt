package `in`.jiffycharge.gopower.view.walletpay

import `in`.jiffycharge.gopower.databinding.WalletpayadapterlayoutBinding
import `in`.jiffycharge.gopower.view.wallet.WallerPayBean
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class WalletPayAdapter(val context: Context):RecyclerView.Adapter<WalletPayAdapter.MyViewHolder>() {

    private var oldSelectItem: WallerPayBean? = null
    private  var data:MutableList<WallerPayBean>?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletPayAdapter.MyViewHolder {
val inflater=LayoutInflater.from(parent.context)
        val binding=WalletpayadapterlayoutBinding.inflate(inflater)
        return MyViewHolder(binding)



    }
    fun refreshList(Data: MutableList<WallerPayBean>?)
    {
        oldSelectItem = null

        this.data=Data
        notifyDataSetChanged()

    }
    fun getSelectPay(): WallerPayBean? {
        return oldSelectItem
    }
    override fun getItemCount(): Int {
//        return list.content.size
        return data!!.size

    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: WalletPayAdapter.MyViewHolder, position: Int) {
        if (data!![position].isSelect) {
            if (oldSelectItem != data!![position]) {

                oldSelectItem = data!![position]

            }
        }

        holder.setdata(data!![position])
        holder.itemView.setOnClickListener {
            oldSelectItem?.isSelect = false
            data!![position].isSelect=true
            oldSelectItem= data!![position]

            notifyDataSetChanged()



        }



    }


    class MyViewHolder(val binding:WalletpayadapterlayoutBinding):RecyclerView.ViewHolder(binding.root) {

        fun setdata(data: WallerPayBean)
        {
            binding.payLogoIv.setImageResource(data.logo)
            binding.payTextTv.text=data.text
            binding.payHintTv.text=data.hint


            if (data.isSelect)
            {
                binding.payTagIv.visibility=View.VISIBLE

            }else{
                binding.payTagIv.visibility=View.GONE

            }

        }



    }


}