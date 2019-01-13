import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * @author jordan
 *
 */
public class CloudSimProject {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//initialize cloudsim package
		int numUser = 1;
		Calendar cal = Calendar.getInstance();
		boolean traceFlag = false;
		CloudSim.init(numUser, cal, traceFlag);
		
		// Creating Datacenter: 
		Datacenter dc = CreateDataCenter();
		
		//Creating DataCenterBroker:
		DatacenterBroker dcb = null;
		try {
			dcb = new DatacenterBroker("DataCenterBroker1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //END OF CREATING DCBROKER
		
		
		//Creating 40 cloudlets:
		List<Cloudlet> cloudletList = new ArrayList<Cloudlet>();
		long cloudletLength = 40000;
		int pesNumber = 1;
		long cloudletFileSize = 300;
		long cloudletOutputSize = 400;
		UtilizationModelFull fullUtilized = new UtilizationModelFull();
		
		for (int cloudletId=0; cloudletId<40; cloudletId++) {
		/*cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize,
				utilizationModelCpu, utilizationModelRam, utilizationModelBw, record, fileList*/
			Cloudlet newCloudlet = new Cloudlet(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize,
					fullUtilized, fullUtilized, fullUtilized);
			/*every cloudlet is associated with somekind of user n the user is represented through the broker object,
			  so we have to take care that u set a userID for each cloudlet before u add that to the list */
			newCloudlet.setUserId(dcb.getId());
			cloudletList.add(newCloudlet);
		} //END OF CREATING CLOUDLETS
		
		//Making 40 VMs (define the procedure for Task Scheduling Algorithm):
		long diskSize = 20000;
		int ram = 2000;
		int mips = 1000;
		int bandwith = 1000;
		int vCPU = 1; // processing CPU
		String VMM = "XEN";
		
		List<Vm> vmList = new ArrayList<Vm>();
		for (int vmId=0; vmId<40; vmId++) {
			Vm virtualMachine = new Vm(vmId, dcb.getId(), mips, vCPU, ram, bandwith, diskSize, VMM, 
					new CloudletSchedulerTimeShared());
			vmList.add(virtualMachine);
		}
		//after creating cloudlet, we need to submit it to a particular broker
		dcb.submitCloudletList(cloudletList);
		dcb.submitVmList(vmList);
		//END OF CREATING VM
		
		// Starts the simulation
		CloudSim.startSimulation();
		
		/*to capture various update which happened during the simulation process: getCloudletReceivedList
		 -> the final status of the cloudlet in execution*/
		List<Cloudlet> finalCloudletExecutionResults = dcb.getCloudletReceivedList();
		
		CloudSim.stopSimulation();
		
		//Print Results:
		int cloudletNum = 0;
		for (Cloudlet cloudlet: finalCloudletExecutionResults) {
			Log.printLine("Result of cloudlet No. " + cloudletNum);
			Log.printLine("***************************************");
			Log.printLine("CloudletID: "+ cloudlet.getCloudletId());
			Log.printLine("VM: " + cloudlet.getVmId());
			Log.printLine("Status: " + cloudlet.getStatus());
			Log.printLine("Execution Time: " + cloudlet.getActualCPUTime());
			Log.printLine("Start: " + cloudlet.getExecStartTime());
			Log.printLine("End: " + cloudlet.getFinishTime());
			Log.printLine("***************************************");
			cloudletNum++;
		}
		
	}
	
	private static Datacenter CreateDataCenter() {
		//making processing element list
		List<Pe> peList = new ArrayList<Pe>();
		
		PeProvisionerSimple pProvisioner = new PeProvisionerSimple(1000);
		
		//ID, and peProvisioner = how much MIPS?
		Pe core1 = new Pe(0, pProvisioner);
		peList.add(core1);
		Pe core2 = new Pe(1, pProvisioner);
		peList.add(core2);
		Pe core3 = new Pe(2, pProvisioner);
		peList.add(core3);
		Pe core4 = new Pe(3, pProvisioner);
		peList.add(core4);
		
		//Because this processor can be distributed in a server or host, make a hostlist 
		List<Host> hostList = new ArrayList<Host>();
		
		int ram = 8000;
		int bw = 8000;
		long storage = 100000;
		//id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler -> bw = bandwith
		//vmScheduler = set how the VM is going to be
		Host host1 = new Host(0,new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, 
				peList, new VmSchedulerSpaceShared(peList));
		hostList.add(host1);
		Host host2 = new Host(1,new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, 
				peList, new VmSchedulerSpaceShared(peList));
		hostList.add(host2);
		Host host3 = new Host(2,new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, 
				peList, new VmSchedulerSpaceShared(peList));
		hostList.add(host3);
		Host host4 = new Host(3,new RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage, 
				peList, new VmSchedulerSpaceShared(peList));
		hostList.add(host4);
		
		//making data center characteristics
		String architecture = "x86"; 
		String os = "Linux";
		String vmm = "XEM";
		double timeZone = 7.0;
		double costPerSec = 3.0;
		double costPerMem = 1.0;
		double costPerStorage = 0.05;
		double costPerBw = 0.10;
		DatacenterCharacteristics dcChara = new DatacenterCharacteristics(architecture, os, vmm, hostList, timeZone, 
				costPerSec, costPerMem, costPerStorage, costPerBw);
		
		//Datacenter
		Datacenter dc = null;
		
		LinkedList<Storage> SANstorage = new LinkedList<Storage>();
		/*params: name, characteristic, vmallocationpolicy, storage=SAN storage boxes, 
		4th param: the delay which is supposed to be done in any to it in 2 different events. u add 1, 1 unit time is added
		automatically between every2 individual event which are going to be executed by the cloudsim*/
		try {
			dc = new Datacenter("Datacenter1", dcChara, new VmAllocationPolicySimple(hostList), SANstorage, 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dc;
	}
}
