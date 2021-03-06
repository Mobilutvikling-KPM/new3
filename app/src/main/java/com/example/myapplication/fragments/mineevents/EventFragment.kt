package com.example.myapplication.event

import RecyclerView.RecyclerView.KommentarRecyclerAdapter
import RecyclerView.RecyclerView.Moduls.DataSourcePerson
import RecyclerView.RecyclerView.Moduls.Event
import RecyclerView.RecyclerView.Moduls.Kommentar
import RecyclerView.RecyclerView.Moduls.Person
import RecyclerView.RecyclerView.OnKommentarItemClickListener
import RecyclerView.RecyclerView.PersonRecyclerAdapter
import RecyclerView.RecyclerView.TopSpacingItemDecoration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.example.myapplication.R
import com.example.myapplication.fragments.mineevents.ModelEvent
import com.example.myapplication.viewmodels.KommentarViewModel
import com.example.myapplication.viewmodels.PersonViewModel
import com.example.myapplication.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.fragment_event.view.*
import kotlinx.android.synthetic.main.fragment_venner.*
import kotlinx.android.synthetic.main.fragment_venner.view.*
import kotlinx.android.synthetic.main.layout_event_list_item.view.*


/**
 * Event fragment som viser ett enkelt event og dens
 */
class EventFragment : Fragment(), OnKommentarItemClickListener {

    //    private lateinit var binding: FragmentEventBinding

    private lateinit var kommentarAdapter: KommentarRecyclerAdapter
    private lateinit var kommentarViewModel: KommentarViewModel
    lateinit var sendtBundle: Event
    var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

  sendtBundle = arguments?.getParcelable<Event>("Event")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



//        //DataBinding i ett fragment -- SKIFT TIL DATABINDING SENERE
//        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_event, container, false)
//
//        binding.modelEvent = model
//
//        binding.apply {
//            model?.tittel = "Dette er test nr2"
//            invalidateAll()
//        }

        val view = inflater.inflate(R.layout.fragment_event, container, false)

        //Lager en viewModel med argumenter
        val viewModelFactory = ViewModelFactory(1)

        //Sender inn viewModel
        kommentarViewModel = ViewModelProvider(this, viewModelFactory).get(KommentarViewModel::class.java)

        //Observerer endringer i event listen
        kommentarViewModel.getKommentarer().observe(viewLifecycleOwner, Observer {
            kommentarAdapter.notifyDataSetChanged()
        })

        //observerer endring i data, og blir trigget dersom det skjer noe
        kommentarViewModel.getIsUpdating().observe(viewLifecycleOwner, Observer {
            //Show og hide progress bar if isUpdating false osv.
            view.recycler_view_kommentar.smoothScrollToPosition((kommentarViewModel.getKommentarer().value?.size
                ?: 0) -1)
        })

        //løsning uten databinding og modelview
        view.tittel.text = sendtBundle.title
        view.dato_og_tid.text = sendtBundle.dato
        view.klokkeSlett.text = " klokken " + sendtBundle.klokke
        view.by.text = sendtBundle.sted
        view.brukernavn_event.text = sendtBundle.forfatter.brukernavn
        view.beskrivelse.text = sendtBundle.body
        view.event_kategori.text = sendtBundle.kategori
        view.button_se_andre_påmeldte.text = sendtBundle.antPåmeldte + " påmeldte"
        var bildeAdresse = sendtBundle.image
        var personBildeAdresse = sendtBundle.forfatter.profilBilde



        //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_baseline_comment_24)

        Glide.with(this@EventFragment)
            .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
            .load(personBildeAdresse) //hvilket bilde som skal loades
            .into(view.bilde_profil_event) //Hvor vi ønsker å loade bildet inn i

        Glide.with(this@EventFragment)
            .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
            .load(bildeAdresse) //hvilket bilde som skal loades
            .into(view.frontBilde) //Hvor vi ønsker å loade bildet inn i

        setPlaceholderGoogleMap(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //placeholder logget inn person
        view.button_post_comment.setOnClickListener{
            kommentarViewModel.leggTilKommentar(
                Kommentar(
                    "KV",
                    Person(
                        "PV",
                        "PlaceHolderMEG",
                        "24",
                        "@String/input",
                        "")
                    ,
                    view.kommentar_edit_tekst.text.toString())
            )
        }

        navController = Navigation.findNavController(view) //referanse til navGraph

        initRecyclerView()
        addDataSet()
    }

    //DUMMY DATA
    private fun addDataSet() {
        kommentarAdapter.submitList(kommentarViewModel.getKommentarer().value!!);
    }

    //Initierer og kobler recycleView til activityMain
    private fun initRecyclerView() {
        //Apply skjønner contexten selv.
        recycler_view_kommentar.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration(20)
            addItemDecoration(topSpacingDecoration)
            kommentarAdapter = KommentarRecyclerAdapter(this@EventFragment)
            adapter = kommentarAdapter
        }
    }

        //PLACEHOLDER GOOGLE MAPS IMAGE
        fun setPlaceholderGoogleMap(view: View) {
            //Forteller hva glide skal gjøre dersom det ikke er ett bilde eller det er error
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_baseline_comment_24)

            Glide.with(this@EventFragment)
                .applyDefaultRequestOptions(requestOptions) // putt inn requestOption
                .load("https://leafletjs.com/examples/quick-start/thumbnail.png") //hvilket bilde som skal loades
                .into(view.google_placeholder_image) //Hvor vi ønsker å loade bildet inn i
        }

    override fun onItemClick(item: Kommentar, position: Int) {
      Toast.makeText(activity,item.person.brukernavn,Toast.LENGTH_SHORT).show()

        navController!!.navigate(R.id.action_eventFragment2_to_besoekProfilFragment)

    }


}