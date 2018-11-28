package com.handrisunjaya.movies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {
    public RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;
    EditText cariMovie;
    Button buttonMovie;

    public static final String EXTRA_MOVIE = "movie";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.content_main );

        mRecyclerView = (RecyclerView) findViewById( R.id.recyclerView );
        mRecyclerView.setLayoutManager( new GridLayoutManager( this, 2 ) );
        mAdapter = new MoviesAdapter( this );
        mRecyclerView.setAdapter( mAdapter );
        getPopularMovies();

        cariMovie = (EditText)findViewById(R.id.cari_movie);
        buttonMovie = (Button)findViewById(R.id.btn_movie);

        buttonMovie.setOnClickListener(myListener);
    }

    View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String title = cariMovie.getText().toString();

            if (TextUtils.isEmpty(title))return;
            pencarianMovies(title);

        }
    };

    private void getPopularMovies() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint( "http://api.themoviedb.org/3" )
                .setRequestInterceptor( new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addEncodedQueryParam( "api_key", "b5ab591b6b32719f1c51024d34facca6" );
                    }
                } )
                .setLogLevel( RestAdapter.LogLevel.FULL )
                .build();
        MoviesApiService service = restAdapter.create( MoviesApiService.class );
        service.getPopularMovies( new Callback<Movie.MovieResult>() {

            @Override
            public void success(Movie.MovieResult movieResult, Response response) {
                mAdapter.setMovieList( movieResult.getResults() );
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        } );
    }

    private void pencarianMovies(final String search) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint( "http://api.themoviedb.org/3/" )
                .setRequestInterceptor( new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addEncodedQueryParam( "api_key", "b5ab591b6b32719f1c51024d34facca6" );
                        request.addEncodedQueryParam( "language","en-US" );
                        request.addEncodedQueryParam( "query",search);
                    }
                } )
                .setLogLevel( RestAdapter.LogLevel.FULL )
                .build();
        MoviesApiService service = restAdapter.create( MoviesApiService.class );
        service.pencarianMovies( new Callback<Movie.MovieResult>() {

            @Override
            public void success(Movie.MovieResult movieResult, Response response) {
                mAdapter.setMovieList( movieResult.getResults() );
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        } );
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        MovieViewHolder(View itemView) {
            super( itemView );
            imageView = (ImageView) itemView.findViewById( R.id.imageView );
        }
    }

    public class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder> {
        private List<Movie> mMovieList;
        private LayoutInflater mInflater;
        private Context mContext;

        MoviesAdapter(Context context) {
            this.mContext = context;
            this.mInflater = LayoutInflater.from( context );
            this.mMovieList = new ArrayList<>();
        }

        @NonNull
        @Override
        public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate( R.layout.row_movie, parent, false );
            MovieViewHolder viewHolder;
            viewHolder = new MovieViewHolder( view );
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
            final Movie movie = mMovieList.get( position );

            // This is how we use Picasso to load images from the internet.
            Picasso.with( mContext )
                    .load( movie.getPoster() )
                    .into( holder.imageView );

            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText( mContext, movie.getTitle(), Toast.LENGTH_SHORT ).show();
                    Intent moveIntent = new Intent( MainActivity.this, MovieDetailActivity.class );
                    moveIntent.putExtra( MovieDetailActivity.EXTRA_MOVIE, movie );
                    startActivity( moveIntent );
                }
            } );
        }

        @Override
        public int getItemCount() {
            return (mMovieList == null) ? 0 : mMovieList.size();
        }

        void setMovieList(List<Movie> movieList) {
            this.mMovieList.clear();
            this.mMovieList.addAll( movieList );
            notifyDataSetChanged();
        }
    }
}
