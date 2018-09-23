package com.example.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

final class StaticResourceHandler {

	private static final String PATH_TO_STATIC_RESOURCES = "<path-to-static-resources>";

	static BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> serveResource() {
		return (req, res) -> {
			String uri = req.path();
			Path path = Paths.get(PATH_TO_STATIC_RESOURCES + ("".equals(uri) ? "index.html" : uri));

			AsynchronousFileChannel channel;
			try {
				channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
			} catch (IOException e) {
				return Mono.error(e);
			}

			return res.send(
					Flux.create(fluxSink -> {
						fluxSink.onDispose(() -> {
							try {
								if (channel != null) {
									channel.close();
								}
							} catch (IOException ignored) {
							}
						});

						ByteBuffer buf = ByteBuffer.allocate(4096);
						channel.read(buf, 0, buf, new CompletionHandlerImpl(channel, fluxSink, 4096));
					}));
		};
	}

	private static final class CompletionHandlerImpl implements CompletionHandler<Integer, ByteBuffer> {

		private final AsynchronousFileChannel channel;

		private final FluxSink<ByteBuf> sink;

		private final int chunk;

		private AtomicLong position;

		CompletionHandlerImpl(AsynchronousFileChannel channel, FluxSink<ByteBuf> sink, int chunk) {
			this.channel = channel;
			this.sink = sink;
			this.chunk = chunk;
			this.position = new AtomicLong(0);
		}

		@Override
		public void completed(Integer read, ByteBuffer dataBuffer) {
			if (read != -1) {
				long pos = this.position.addAndGet(read);
				dataBuffer.flip();
				ByteBuf buf = ByteBufAllocator.DEFAULT.buffer().writeBytes(dataBuffer);
				this.sink.next(buf);

				if (!this.sink.isCancelled()) {
					ByteBuffer newByteBuffer = ByteBuffer.allocate(chunk);
					this.channel.read(newByteBuffer, pos, newByteBuffer, this);
				}
			}
			else {
				try {
					channel.close();
				}
				catch (IOException ignored) {
				}
				this.sink.complete();
			}
		}

		@Override
		public void failed(Throwable exc, ByteBuffer dataBuffer) {
			try {
				channel.close();
			}
			catch (IOException ignored) {
			}
			this.sink.error(exc);
		}
	}
}
