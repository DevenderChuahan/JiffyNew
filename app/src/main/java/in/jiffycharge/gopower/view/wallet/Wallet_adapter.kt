package `in`.jiffycharge.gopower.view.wallet

import `in`.jiffycharge.gopower.databinding.WalletHistoryAdapterLayoutBinding
import `in`.jiffycharge.gopower.model.ContentX
import `in`.jiffycharge.gopower.model.Wallet_history_model
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class Wallet_adapter(val context: Context, val list: Wallet_history_model) :
    RecyclerView.Adapter<Wallet_adapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WalletHistoryAdapterLayoutBinding.inflate(inflater)


        return MyViewHolder(binding)
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItemCount(): Int {
        return list.content.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binditms(list.content.get(position))
    }
    class MyViewHolder(val binding: WalletHistoryAdapterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binditms(item: ContentX) {
            try {
                binding.tvTopUpType.text = item.logInfo
                binding.tvAmount.text = item.logAmount.toString()
                val sdftime = SimpleDateFormat("h:mm a")
                val sdf = SimpleDateFormat("dd MMM, yyyy")
                val netDate = Date(item.createTime)
                val date = sdf.format(netDate)
                val time = sdftime.format(netDate)
                binding.tvDate.text = date
                binding.tvTime.text = time
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}