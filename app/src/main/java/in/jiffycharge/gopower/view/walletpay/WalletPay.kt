package `in`.jiffycharge.gopower.view.walletpay
import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.databinding.FragmentWalletBinding
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.view.wallet.WallerPayBean
import `in`.jiffycharge.gopower.viewmodel.WalletPayViewModel
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_pay_wallet.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletPay : Fragment() {
    private val Wallet_Pay_View_Model by viewModel<WalletPayViewModel>()
    lateinit var context: Activity
lateinit var walletpayAdapter:WalletPayAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context = requireContext() as Activity
//        val bind = DataBindingUtil.inflate<FragmentWalletBinding>(
//            inflater,
//            R.layout.fragment_pay_wallet,
//            container,
//            false
//        )
//            .apply {
//                this.lifecycleOwner = this@WalletPay
//            }
//        return bind.root

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pay_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
          walletpayAdapter = WalletPayAdapter(context)

        Wallet_Pay_View_Model.getSupportPay()
        Wallet_Pay_View_Model.paydata.observe(viewLifecycleOwner, Observer {
            when(it.status)
            {
                Resourse.Status.SUCCESS->
                {

//                    adapter.refreshList(bean.data!!)
                    rv_wallet_pay.adapter=walletpayAdapter.apply {
                        this.refreshList(it.data)
                        notifyDataSetChanged()


                    }




                }


            }





        })


    }


    fun getWallerPayBean(): WallerPayBean? {
        return walletpayAdapter.getSelectPay()
    }
}