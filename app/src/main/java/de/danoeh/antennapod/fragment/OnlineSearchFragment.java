package de.danoeh.antennapod.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alan.alansdk.AlanCallback;
import com.alan.alansdk.AlanState;
import com.alan.alansdk.events.EventCommand;
import com.alan.alansdk.events.EventOptions;
import com.alan.alansdk.events.EventParsed;
import com.alan.alansdk.events.EventRecognised;
import com.alan.alansdk.events.EventText;

import org.json.JSONObject;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.OnlineFeedViewActivity;
import de.danoeh.antennapod.adapter.itunes.ItunesAdapter;
import de.danoeh.antennapod.alan.Alan;
import de.danoeh.antennapod.alan.data.AlanCommand;
import de.danoeh.antennapod.discovery.PodcastSearchResult;
import de.danoeh.antennapod.discovery.PodcastSearcher;
import de.danoeh.antennapod.discovery.PodcastSearcherRegistry;
import io.reactivex.disposables.Disposable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineSearchFragment extends Fragment {

    private static final String TAG = "FyydSearchFragment";
    private static final String ARG_SEARCHER = "searcher";
    private static final String ARG_QUERY = "query";

    /**
     * Adapter responsible with the search results
     */
    private ItunesAdapter adapter;
    private PodcastSearcher searchProvider;
    private GridView gridView;
    private ProgressBar progressBar;
    private TextView txtvError;
    private Button butRetry;
    private TextView txtvEmpty;

    /**
     * List of podcasts retrieved from the search
     */
    private List<PodcastSearchResult> searchResults;
    private Disposable disposable;

    static final int ACTIVITY_FEED_RESULT = 100;

    public static OnlineSearchFragment newInstance(Class<? extends PodcastSearcher> searchProvider) {
        return newInstance(searchProvider, null);
    }

    public static OnlineSearchFragment newInstance(Class<? extends PodcastSearcher> searchProvider, String query) {
        OnlineSearchFragment fragment = new OnlineSearchFragment();
        Bundle arguments = new Bundle();
        arguments.putString(ARG_SEARCHER, searchProvider.getName());
        arguments.putString(ARG_QUERY, query);
        fragment.setArguments(arguments);
        return fragment;
    }

    /**
     * Constructor
     */
    public OnlineSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        for (PodcastSearcherRegistry.SearcherInfo info : PodcastSearcherRegistry.getSearchProviders()) {
            if (info.searcher.getClass().getName().equals(getArguments().getString(ARG_SEARCHER))) {
                searchProvider = info.searcher;
                break;
            }
        }
        if (searchProvider == null) {
            throw new IllegalArgumentException("Podcast searcher not found");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_itunes_search, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));
        gridView = root.findViewById(R.id.gridView);
        adapter = new ItunesAdapter(getActivity(), new ArrayList<>());
        gridView.setAdapter(adapter);

        //Show information about the podcast when the list item is clicked
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            PodcastSearchResult podcast = searchResults.get(position);
            Intent intent = new Intent(getActivity(), OnlineFeedViewActivity.class);
            intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, podcast.feedUrl);
//            startActivity(intent);
            startActivityForResult(intent, ACTIVITY_FEED_RESULT);
        });
        progressBar = root.findViewById(R.id.progressBar);
        txtvError = root.findViewById(R.id.txtvError);
        butRetry = root.findViewById(R.id.butRetry);
        txtvEmpty = root.findViewById(android.R.id.empty);

        txtvEmpty.setText(getString(R.string.search_powered_by, searchProvider.getName()));
        initializeAlanListener();
        return root;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
        adapter = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.online_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView sv = (SearchView) MenuItemCompat.getActionView(searchItem);
        sv.setQueryHint(getString(R.string.search_podcast_hint));
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                sv.clearFocus();
                search(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            }
        });
        searchItem.expandActionView();

        if (getArguments().getString(ARG_QUERY, null) != null) {
            sv.setQuery(getArguments().getString(ARG_QUERY, null), true);
        }

    }

    private void search(String query) {
        if (disposable != null) {
            disposable.dispose();
        }
        showOnlyProgressBar();
        disposable = searchProvider.search(query).subscribe(result -> {
            searchResults = result;
            progressBar.setVisibility(View.GONE);
            adapter.clear();
            adapter.addAll(searchResults);
            adapter.notifyDataSetInvalidated();
            gridView.setVisibility(!searchResults.isEmpty() ? View.VISIBLE : View.GONE);
            txtvEmpty.setVisibility(searchResults.isEmpty() ? View.VISIBLE : View.GONE);
            txtvEmpty.setText(getString(R.string.no_results_for_query, query));
            String alanMsg = "Completed the search, there are " +  searchResults.size() + " results.";
            Map<String, String> resultsData = new HashMap<>();
            resultsData.put("count", "" + searchResults.size());
            JSONObject jsonObject = new JSONObject(resultsData);

            Alan.getInstance().callProjectApi("setPodcastResultCount", jsonObject.toString());

            if(searchResults.isEmpty())
                alanMsg = getString(R.string.no_results_for_query, query).replaceAll("\"", " ");

            Alan.getInstance().playText(alanMsg);
        }, error -> {
                Log.e(TAG, Log.getStackTraceString(error));
                progressBar.setVisibility(View.GONE);
                txtvError.setText(error.toString());
                txtvError.setVisibility(View.VISIBLE);
                butRetry.setOnClickListener(v -> search(query));
                butRetry.setVisibility(View.VISIBLE);
                Alan.getInstance().playText(txtvError.getText().toString());
        });
    }

    private void showOnlyProgressBar() {
        gridView.setVisibility(View.GONE);
        txtvError.setVisibility(View.GONE);
        butRetry.setVisibility(View.GONE);
        txtvEmpty.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHiddenChanged(boolean hiddenState){
        if(!hiddenState)
            initializeAlanListener();
    }

    /**
     * method sets the listener and visual state for the alan SDK.
     */
    private void initializeAlanListener(){
        Alan.getInstance().setVisualState("Podcast Results");
        /**
         * Clearing the previous callbacks from the Alan if any, before registering new one.
         * Because, no other call back should active, only the present view call back should be active.
         */

        Alan.getInstance().clearCallbacks();

        Alan.getInstance().registerCallback(new AlanCallback() {
            @Override
            public void onAlanStateChanged(@NonNull AlanState alanState) {
                super.onAlanStateChanged(alanState);
            }

            @Override
            public void onRecognizedEvent(EventRecognised eventRecognised) {
                super.onRecognizedEvent(eventRecognised);
            }

            @Override
            public void onParsedEvent(EventParsed eventParsed) {
                super.onParsedEvent(eventParsed);
            }

            @Override
            public void onOptionsReceived(EventOptions eventOptions) {
                super.onOptionsReceived(eventOptions);
            }

            @Override
            public void onCommandReceived(EventCommand eventCommand) {
                super.onCommandReceived(eventCommand);
                AlanCommand alanCommand = Alan.getInstance().processAlanEventCommand(eventCommand);
                handleAlanVoiceCommand(alanCommand);
            }

            @Override
            public void onTextEvent(EventText eventText) {
                super.onTextEvent(eventText);
            }

            @Override
            public void onEvent(String event, String payload) {
                super.onEvent(event, payload);
            }

            @Override
            public void onError(String error) {
                super.onError(error);
            }
        });
    }

    /**
     * Method handles the alan voice commands and process the event actions associated with the voice commands.
     *
     * @param alanCommand
     */
    public void handleAlanVoiceCommand(AlanCommand alanCommand) {
        if(!alanCommand.hasError()) {
            String cmd = alanCommand.getCommand();
            String value = alanCommand.getValue();
            switch(cmd) {
                case "go-back":
                    EditText combinedFeedSearchBox = getActivity().findViewById(R.id.combinedFeedSearchBox);
                    combinedFeedSearchBox.setText("", TextView.BufferType.EDITABLE);
                    getActivity().getSupportFragmentManager().popBackStack();
                    break;
                case "select-podcast":
                    if (searchResults != null) {
                        Alan.getInstance().getAlanButton().playText("Fetching the podcast details");
                        Integer pos = Integer.parseInt(value) - 1;
                        PodcastSearchResult podcast = searchResults.get(pos);
                        Intent intent = new Intent(getActivity(), OnlineFeedViewActivity.class);
                        intent.putExtra(OnlineFeedViewActivity.ARG_FEEDURL, podcast.feedUrl);
                        startActivityForResult(intent, ACTIVITY_FEED_RESULT);
                    }
                    break;
            }
        } else{
            Alan.getInstance().playText(alanCommand.getError());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        initializeAlanListener();
    }
}
