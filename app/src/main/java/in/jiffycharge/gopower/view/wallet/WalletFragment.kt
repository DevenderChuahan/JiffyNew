package `in`.jiffycharge.gopower.view.wallet
import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.databinding.FragmentWalletBinding
import `in`.jiffycharge.gopower.di.wallet_view_model
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.utils.toast
import `in`.jiffycharge.gopower.view.charge.ChargeActivity
import `in`.jiffycharge.gopower.view.deposit.DepositActivity
import `in`.jiffycharge.gopower.viewmodel.HomeActivityViewModel
import `in`.jiffycharge.gopower.viewmodel.WalletViewModel
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_deposit.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_wallet.*
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.getViewModel

/**
 * A simple [Fragment] subclass.
 */
class WalletFragment : Fragment() {
    private val Wallet_View_Model by viewModel<WalletViewModel>()
    val home_view_model by viewModel<HomeActivityViewModel>()

    lateinit var context: Activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context = requireContext() as Activity
        val bind = DataBindingUtil.inflate<FragmentWalletBinding>(
            inflater,
            R.layout.fragment_wallet,
            container,
            false
        )
            .apply {
                this.setLifecycleOwner(this@WalletFragment)
                this.walletVmodel = Wallet_View_Model
            }
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wallet_loader.visibility = View.VISIBLE

        home_view_model.repo.fetchUserprofile()
        home_view_model.repo._data.observe(this.requireActivity(), Observer { userprofile ->
            requireActivity().runOnUiThread {


                when (userprofile.status) {
                    Resourse.Status.LOADING -> {

                    }

                    Resourse.Status.SUCCESS -> {
                        if (userprofile.data!!.item.deposit>0) {
                            tv_deposit_Amount.text = userprofile.data!!.item.deposit.toString()
                        }else
                        {
                            tv_deposit_Amount.text = userprofile.data!!.item.deposit.toString()

                            tv_withdraw.text="Deposit refundable amount"
                        }
                    }
                    Resourse.Status.ERROR -> {
                        requireActivity().toast(userprofile.data?.error_description.toString())

                    }

                }


            }


        })











        Wallet_View_Model.walletRepository.response_message.observe(viewLifecycleOwner, Observer {
            if (it.equals("200")) {
                Wallet_View_Model.walletRepository.wallet_history_data.observe(
                    viewLifecycleOwner,
                    Observer { wallet_history_model ->
                        wallet_rv.apply {
                            requireActivity().runOnUiThread {
                                if (wallet_history_model.content.isEmpty()) {
                                    wallet_loader.visibility = View.GONE
                                    ll_wallet_no_history.visibility = View.VISIBLE
                                } else {
                                    wallet_loader.visibility = View.GONE
                                    adapter = Wallet_adapter(context, wallet_history_model)
                                }
                            }
                        }
                    })
            } else {
                wallet_loader.visibility = View.GONE
                ll_wallet_no_history.visibility = View.VISIBLE
            }
        })

        wallet_back.setOnClickListener {
            context.onBackPressed()
        }

        ll_addFund.setOnClickListener {
            val intent = Intent(context, ChargeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        tv_withdraw.setOnClickListener {

            val intent = Intent(context, DepositActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)


        }





    }




}
