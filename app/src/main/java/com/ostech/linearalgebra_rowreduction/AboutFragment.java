package com.ostech.linearalgebra_rowreduction;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.*;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {
    private TextView versionNameText;
    private TextView githubRepositoryText;
    private TextView emailAddressText;
    private TextView twitterHandleText;
    private TextView githubProfileText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        versionNameText = view.findViewById(R.id.version_name_text);
        String versionName = BuildConfig.VERSION_NAME;
        versionNameText.setText(versionName);

        githubRepositoryText = view.findViewById(R.id.github_repository_link_text);
        githubRepositoryText.setMovementMethod(LinkMovementMethod.getInstance());

        emailAddressText = view.findViewById(R.id.email_address_text);
        emailAddressText.setMovementMethod(LinkMovementMethod.getInstance());

        twitterHandleText = view.findViewById(R.id.twitter_handle_text);
        twitterHandleText.setMovementMethod(LinkMovementMethod.getInstance());

        githubProfileText = view.findViewById(R.id.github_profile_text);
        githubProfileText.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.about_menu_item_title);
    }
}
