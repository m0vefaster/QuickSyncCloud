import java.io.Serializable;
class Message implements Serializable {
	Object obj;
	int len;

	Message(Object obj) {
		this.obj = obj;
	}
}