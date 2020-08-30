package `in`.jiffycharge.gopower.view.wallet
import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.databinding.FragmentWalletBinding
import `in`.jiffycharge.gopower.di.wallet_view_model
import `in`.jiffycharge.gopower.viewmodel.WalletViewModel
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
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
    }




}
