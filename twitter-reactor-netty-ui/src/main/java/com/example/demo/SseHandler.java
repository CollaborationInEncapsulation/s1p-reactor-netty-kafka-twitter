package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;
import reactor.netty.NettyPipeline;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStreamFactory;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

final class SseHandler {

	static BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> serveSse() {
		ObjectMapper mapper = new ObjectMapper();
		Function<Tweet, ByteBuf> jsonEncoder = tweet -> {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				out.write("data: ".getBytes());
				mapper.writeValue(out, tweet);
				out.write("\n\n".getBytes());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return ByteBufAllocator.DEFAULT.buffer().writeBytes(out.toByteArray());
		};

		return (req, res) ->
				res.header("Content-Type", "text/event-stream")
						.options(NettyPipeline.SendOptions::flushOnEach)
						.send(tweetsFlux.map(jsonEncoder));
	}

	private static final Flux<Tweet> tweetsFlux = Flux.<Tweet>create(sink ->
			new TwitterStreamFactory().getInstance().addListener(new StatusListener() {

				private final ReactorNettyCallFactory factory = new ReactorNettyCallFactory();

				@Override
				public void onStatus(Status status) {
					if (status.getGeoLocation() != null) {
						double[] location = new double[]{
								status.getGeoLocation().getLongitude(),
								status.getGeoLocation().getLatitude()
						};
						emitTweet(status, location);
					}

					if (status.getPlace() != null) {
						double[] location = new double[]{
								status.getPlace().getGeometryCoordinates()[0][0].getLongitude(),
								status.getPlace().getGeometryCoordinates()[0][0].getLatitude()
						};
						emitTweet(status, location);
					}

					if (status.getUser().getLocation() != null) {
						MapboxGeocoding client = MapboxGeocoding.builder()
								.accessToken("<token>")
								.query(status.getUser().getLocation())
								.autocomplete(true)
								.geocodingTypes(GeocodingCriteria.TYPE_POI)
								.mode(GeocodingCriteria.MODE_PLACES)
								.build();

						client.setCallFactory(factory);

						client.enqueueCall(new Callback<GeocodingResponse>() {
							@Override
							public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
								GeocodingResponse body = response.body();
								if (body == null || body.features().isEmpty()) {
									return;
								}
								double[] location = new double[]{
										body.features().get(0).center().longitude(),
										body.features().get(0).center().latitude()
								};
								emitTweet(status, location);
							}

							@Override
							public void onFailure(Call<GeocodingResponse> call, Throwable t) {

							}
						});
					}
				}

				@Override
				public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

				}

				@Override
				public void onTrackLimitationNotice(int numberOfLimitedStatuses) {

				}

				@Override
				public void onScrubGeo(long userId, long upToStatusId) {

				}

				@Override
				public void onStallWarning(StallWarning warning) {

				}

				@Override
				public void onException(Exception ex) {

				}

				private void emitTweet(Status status, double[] location) {
					sink.next(new Tweet(
							status.getText(),
							location,
							Arrays.stream(status.getHashtagEntities())
									.map(HashtagEntity::getText)
									.toArray(String[]::new)
					));
				}
			}).filter(
					"S1P",
					"SpringOnePlatform",
					"SpringFramework",
					"Java",
					"ReactiveJava",
					"RxJava",
					"Kotlin",
					"RxKotlin",
					"ProjectReactor",
					"ReactiveProgramming",
					"ReactiveSystem",
					"ApacheKafka",
					"Kafka",
					"Akka",
					"AkkaStreams",
					"Alpakka",
					"ReactiveKafka",
					"Scala",
					"RxScala",
					"ReactorScala",
					"ReactiveStreams",
					"Netty",
					"ReactorNetty",
					"RxNetty",
					"WebFlux",
					"Programming",
					"Coding",
					"JavaScript",
					"JS",
					"Computer",
					"Science",
					"Software",
					"RxJs",
					"JDBC",
					"R2DBC"
			))
			.subscribeOn(Schedulers.elastic())
			.log()
			.subscribeWith(ReplayProcessor.create(30));
}
