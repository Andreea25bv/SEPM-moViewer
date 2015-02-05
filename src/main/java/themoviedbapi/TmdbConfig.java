package themoviedbapi;

import themoviedbapi.model.config.ConfigResults;
import themoviedbapi.tools.ApiUrl;


class TmdbConfig extends AbstractTmdbApi {

    public static final String TMDB_METHOD_CONFIGURATION = "configuration";


    TmdbConfig(TmdbApi tmdbApi) {
        super(tmdbApi);
    }


    public ConfigResults getConfig() {
        return mapJsonResult(new ApiUrl(TMDB_METHOD_CONFIGURATION), ConfigResults.class);
    }
}
