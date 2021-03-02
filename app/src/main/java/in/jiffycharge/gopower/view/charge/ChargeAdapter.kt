package `in`.jiffycharge.gopower.view.charge

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.databinding.ChargelayoutBinding
import `in`.jiffycharge.gopower.model.ItemXXXXXXXXXXXX
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus

class ChargeAdapter(
    val context: ChargeActivity,
   val  homeViewModel: HomeActivityViewModel
) : RecyclerView.Adapter<ChargeAdapter.Myviewholder>() {
    var data:List<ItemXXXXXXXXXXXX>?=null
    var seleteditem:ItemXXXXXXXXXXXX?=null
    private var selectItemPosition = 0
     private  var lastSelectedPosition=-1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChargeAdapter.Myviewholder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChargelayoutBinding.inflate(inflater)
        return ChargeAdapter.Myviewholder(binding)
    }

    fun refreshList(Data: List<ItemXXXXXXXXXXXX>)
    {
        this.data=Data
        notifyDataSetChanged()


    }
    fun getSelectData():ItemXXXXXXXXXXXX
    {
        return this.seleteditem!!


    }

    override fun getItemCount(): Int {
        return data?.size?:0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun onBindViewHolder(holder: ChargeAdapter.Myviewholder, position: Int) {
        holder.bindItems(data!!.get(position))
        holder.binding.llOrdersView.setOnClickListener {
            homeViewModel.repo.adapteritemlist.postValue(data!!.get(position))
            lastSelectedPosition=position
            EventBus.getDefault().post("ItemSelected")
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

    class Myviewholder(val binding: ChargelayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItems(content: ItemXXXXXXXXXXXX) {


            try {

                binding.tvAmt.text = "${content.payAmount} ${content.currency}"
                if (content.remark.isNullOrBlank()) {
                    binding.tvAmt2.visibility = View.GONE


                }
                else {
                    binding.tvAmt2.visibility = View.VISIBLE
                    binding.tvAmt2.text = "${content.remark}"

                }


                binding.executePendingBindings()



            }
            catch (e:Exception)
        {

        }
    }




}


}