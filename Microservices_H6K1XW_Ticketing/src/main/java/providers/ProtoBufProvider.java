package providers;

import java.io.IOException;
import java.io.InputStream;
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

import com.google.protobuf.Message;

@Provider
@Consumes("application/x-protobuf")
@Produces("application/x-protobuf")
public class ProtoBufProvider implements MessageBodyReader<Message>, MessageBodyWriter<Message> {
	@Override
	public boolean isReadable(Class cls, Type type, Annotation[] annots, MediaType mediaType) {
		//System.out.println("work");
		return true;
	}

	@Override
	public Message readFrom(Class cls, Type type, Annotation[] annots, MediaType mediaType, MultivaluedMap map,
			InputStream stream) throws IOException, WebApplicationException {
		
		System.out.println("Read Proto input");
		try {
			Method parseFromMethod = cls.getMethod("parseFrom", InputStream.class);
			return (Message) parseFromMethod.invoke(null, stream);
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
		System.out.println("Write Proto output");
		message.writeTo(stream);
	}
}