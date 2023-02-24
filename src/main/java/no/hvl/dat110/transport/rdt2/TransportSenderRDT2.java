package no.hvl.dat110.transport.rdt2;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import no.hvl.dat110.network.NetworkService;
import no.hvl.dat110.transport.*;
import no.hvl.dat110.transport.rdt21.SegmentRDT21;

public class TransportSenderRDT2 extends TransportSender implements ITransportProtocolEntity {

	public enum RDT2SenderStates {
		WAITDATA0, WAITDATA1, WAITACKNAK0, WAITACKNAK1;
	}

	private LinkedBlockingQueue<SegmentRDT2> recvqueue;
	private RDT2SenderStates state;

	public TransportSenderRDT2(NetworkService ns) {
		super("TransportSender",ns);
		recvqueue = new LinkedBlockingQueue<SegmentRDT2>();
		state = RDT2SenderStates.WAITDATA0;
	}

	public void rdt_recv(Segment segment) {

		System.out.println("[Transport:Sender   ] rdt_recv: " + segment.toString());

		try {
			
			recvqueue.put((SegmentRDT2) segment);
			
		} catch (InterruptedException ex) {
			System.out.println("TransportSenderRDT2 thread " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private byte[] data = null;

	public void doProcess() {

		switch (state) {

		case WAITDATA0:

			doWaitData(0);

			break;

		case WAITDATA1:

			doWaitData(1);

			break;

		case WAITACKNAK0:

			doWaitAckNak(0);

			break;

		case WAITACKNAK1:

			doWaitAckNak(1);

			break;

		default:
			break;
		}

	}

	private void changeState(RDT2SenderStates newstate) {

		System.out.println("[Transport:Sender   ] " + state + "->" + newstate);
		state = newstate;
	}
	
	private void doWaitData(int seqnr) {
		
		try {
			
			data = outdataqueue.poll(2, TimeUnit.SECONDS);

			if (data != null) { // something to send

				udt_send(new SegmentRDT2(data, seqnr));
				
				if (seqnr == 0) {
					changeState(RDT2SenderStates.WAITACKNAK0);
				} else {
					changeState(RDT2SenderStates.WAITACKNAK1);
				}
				
			}

		} catch (InterruptedException ex) {
			System.out.println("TransportSenderRDT2 thread " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void doWaitAckNak(int seqnr) {
		
		try {

			SegmentRDT2 acksegment = recvqueue.poll(2, TimeUnit.SECONDS);

			if (acksegment != null) {

				SegmentType type = acksegment.getType();
				
				if (!acksegment.isCorrect()) {
					
					System.out.println("[Transport:Sender   ] BITERRORS");
					udt_send(new SegmentRDT21(data, seqnr)); // retransmit
					
				} else if (type == SegmentType.NAK) {

					System.out.println("[Transport:Sender   ] NAK ");
					udt_send(new SegmentRDT2(data, seqnr));
					
				} else {
					System.out.println("[Transport:Sender   ] ACK ");
					
					if (seqnr == 0) {
						changeState(RDT2SenderStates.WAITDATA1);
					} else {
						changeState(RDT2SenderStates.WAITDATA0);
					}
					
					data = null;
				}
			}
			
		} catch (InterruptedException ex) {
			System.out.println("TransportSenderRDT2 thread " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
