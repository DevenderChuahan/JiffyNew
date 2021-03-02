package `in`.jiffycharge.gopower.view.orders


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_order.*

import `in`.jiffycharge.gopower.R
import `in`.jiffycharge.gopower.databinding.FragmentOrderBinding
import `in`.jiffycharge.gopower.utils.Resourse
import `in`.jiffycharge.gopower.viewmodel.Orders_view_model
import android.app.Activity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class OrderFragment : Fragment() {

    private  val order_view_Model by viewModel<Orders_view_model>()

    lateinit var context:Activity
    val map_list=HashMap<String,String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        context=requireContext() as Activity

        // Inflate the layout for this fragment
        val bind=DataBindingUtil.inflate<FragmentOrderBinding>(inflater,R.layout.fragment_order,container,false)
            .apply {
               this.setLifecycleOwner(this@OrderFragment)
                this.oviewmodel = order_view_Model

            }

        return  bind.root

//        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Order_loader.visibility=View.VISIBLE


//        rv_my_orders.adapter= OrdersAdapter(context,map_list)

                                order_view_Model.data.observe(viewLifecycleOwner, Observer {orderlistmodel->
                                    rv_my_orders.apply {
                                        requireActivity().runOnUiThread {
                                            when(orderlistmodel.status)
                                            {
                                                Resourse.Status.SUCCESS->
                                                {

                                                    if (orderlistmodel.data!!.content.isEmpty())
                                                    {
                                                        ll_no_orders.visibility=View.VISIBLE
                                                        Order_loader.visibility=View.GONE
                                                    }else
                                                    {
                                                        Order_loader.visibility=View.GONE
                                                        adapter=OrdersAdapter(context,orderlistmodel)

                                                    }
                                                }

                                                Resourse.Status.ERROR->
                                            {

                                                ll_no_orders.visibility=View.VISIBLE
                                                Order_loader.visibility=View.GONE
                                            }

                                            }




                                        }


                                    }


                                })





        myorder_back.setOnClickListener {
            context.onBackPressed()

        }


    }









}
