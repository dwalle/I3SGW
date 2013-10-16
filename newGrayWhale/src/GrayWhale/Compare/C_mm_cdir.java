package GrayWhale.Compare;
import java.io.File;
import java.util.ArrayList;


public class C_mm_cdir {

	public static String path;
	

	public C_mm_cdir(final String str){
		System.out.println("trace-- mm_cdir mm_cdir()");//daniel remove
		path=str;
	}

	//in the funcs below key is interpreted as the pattern 'key'
	public int get_files(ArrayList<String> list, final String key){
		System.out.println("trace--mm_cdir get_files");//daniel remove
		FileCollect(list, key);
		return 0;
	}
	public int get_subdirs(ArrayList<String> list){
		System.out.println("trace-- mm_cdir get_subdirs");//daniel remove
		FolderSearch(list);
		
		return 0;
	}
	
	
	//searches and puts into the arrays list for all files.
	private static void FileCollect(ArrayList<String> list, String key){
		  // Directory path here
		  //String path = "."; 
		System.out.println("trace-- mm_cdir fileSearch");//daniel remove
		 
		File f = new File(path);
		File[] fileList = f.listFiles();
		System.out.println("	all files and folders from: "+path);
		for( int i=0; i<fileList.length;i++){
			if(fileList[i].getAbsolutePath().contains(key)){
				list.add(fileList[i].getAbsolutePath());
			}
		}
	}
	
	//searches through the current folder to see if it contains any other folders.
	private static void FolderSearch(ArrayList<String> list){
		  // Directory path here
		  //String path = "."; 
		System.out.println("trace-- mm_cdir FolderSearch");//daniel remove
		 
		File f = new File(path);
		File[] fileList = f.listFiles();
		for( int i=0; i<fileList.length;i++){
			if(!fileList[i].getAbsolutePath().contains(".")){
				list.add(fileList[i].getAbsolutePath());
			}
		}
		
	}
	public final String get_path(){
		return path;
	}
	
}
