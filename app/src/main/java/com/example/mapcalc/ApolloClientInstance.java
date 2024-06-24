package com.example.mapcalc;

import android.content.Context;

import com.apollographql.apollo.ApolloClient;

import okhttp3.OkHttpClient;

public class ApolloClientInstance {
    private static ApolloClient apolloClient;

    public static ApolloClient getApolloClient(Context context) {
        if (apolloClient == null) {
            apolloClient = ApolloClient.builder()
                    .serverUrl("https://your-graphql-api.com/graphql") // Replace with your GraphQL API URL
                    .okHttpClient(new OkHttpClient.Builder().build())
                    .build();
        }
        return apolloClient;
    }

}
