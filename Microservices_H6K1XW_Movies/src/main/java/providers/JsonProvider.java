package providers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.google.protobuf.util.JsonFormat;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class JsonProvider implements MessageBodyReader<Message>, MessageBodyWriter<Message> {
	

	@Override
	public boolean isReadable(Class cls, Type type, Annotation[] annots, MediaType mediaType) {
		//System.out.println("work");
		return true;
	}

	@Override
	public Message readFrom(Class cls, Type type, Annotation[] annots, MediaType mediaType, MultivaluedMap map,
			InputStream stream) throws IOException, WebApplicationException {
		
		System.out.println("Read JSON input");
		try {
			Method getDefaultInstanceMethod = cls.getMethod("getDefaultInstance");
			Message message = (Message)getDefaultInstanceMethod.invoke(null, null);
			Builder builder = message.toBuilder();
			JsonFormat.parser().merge(convertInputStreamToString(stream), builder);
			return (Message) builder.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getSize(Message message, Class cls, Type type, Annotation[] annots, MediaType mediaType) {
		//System.out.println("work");
		return -1;
	}

	@Override
	public boolean isWriteable(Class cls, Type type, Annotation[] annots, MediaType mediaType) {
		//System.out.println("work");
		return true;
	}

	@Override
	public void writeTo(Message message, Class cls, Type type, Annotation[] annots, MediaType mediaType,
			MultivaluedMap map, OutputStream stream) throws IOException, WebApplicationException {
		System.out.println("Write JSON output");
		String json = JsonFormat.printer().print(message);
		stream.write(json.getBytes());
	}
	

	private String convertInputStreamToString(InputStream io) {
		//System.out.println("work");
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(io));
			String line = reader.readLine();
			while (line != null) {
				sb.append(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to obtain an InputStream", e);

		}
		return sb.toString();
	}

	
	/*
	public static String LS = System.getProperty("line.separator");
	private ExtensionRegistry extensionRegistry = ExtensionRegistry.newInstance();
	
	
	@Override
	public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		System.out.println("1.1");
		return true;
	}
	
	
	@Override
	public Message readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
		System.out.println("1.2");
		try {
			Method newBuilder = type.getMethod("newBuilder");

			GeneratedMessage.Builder builder = (GeneratedMessage.Builder) newBuilder.invoke(type);
			String data = convertInputStreamToString(entityStream);
			JsonFormat.parser().merge(data, builder);
			return builder.build();
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	private String convertInputStreamToString(InputStream io) {
		System.out.println("1.3");
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(io));
			String line = reader.readLine();
			while (line != null) {
				sb.append(line).append(LS);
				line = reader.readLine();
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to obtain an InputStream", e);

		}
		return sb.toString();
	}


	@Override
	public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		System.out.println("1.4");
		return true; //mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE);
	}


	@Override
	public long getSize(Message t, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		System.out.println("1.5");
		return -1;
	}


	@Override
	public void writeTo(Message t, Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
		System.out.println("1.6");
		entityStream.write(JsonFormat.printer().print((Message) t).getBytes());
	}
*/
}
