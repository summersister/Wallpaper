import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.win32.StdCallLibrary;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Demo {

    public static File lastFile = null;

    public static List<File> files = null;

    public static void main(String[] args) {

        List<File> listDir = new ArrayList();

        File[] fs = File.listRoots();

        FileSystemView fsv = FileSystemView.getFileSystemView();

        System.out.println(fs[0].getName());

        for(int i = 0 ; i < fs.length ; i++){

            listDir.add(fs[i]);
        }

        System.out.println("查询到的磁盘 :");

        listDir.forEach(p -> System.out.println(fsv.getSystemDisplayName(p)));

        System.out.println("请输入磁盘名");

        Scanner sc = new Scanner(System.in);

        String next = sc.next();

        for(File f : listDir){

            if(fsv.getSystemDisplayName(f).toLowerCase().contains(next.toLowerCase())){

                select(f);
            }
        }

        System.out.println("您选择的路径为 " + lastFile.getPath());
        System.out.println("请输入更换间隔(毫秒)");

        int i = new Scanner(System.in).nextInt();

        selectFile();

        new Thread(() -> new Timer().schedule(new TimerTask() {

            @Override
            public void run() {

                selectFile();
            }
        }, 10000, 10000));

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {

                int i = new Random().nextInt(files.size());

                String path = files.get(i).getPath();

                System.out.println(path);

                change(path);
            }
        }, 3000, i);


    }

    /**
     * 扫描图片文件
     *
     */
    private static void selectFile() {

        List<File> files = Arrays.asList(lastFile.listFiles());

        Demo.files = files.stream()
                .filter(p -> {

                    String name = p.getName();

                    String hz = name.substring(name.lastIndexOf(".")).toLowerCase();

                    if(p.isFile()
                            && (hz.contains("jpg")
                            || hz.contains("png")
                            || hz.contains("jpeg")
                            || hz.contains("gif"))){

                        return true;
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * 递归录入用户文件路径
     *
     * @param f
     */
    private static void select(File f) {

        List<File> listDir = Arrays.asList(f.listFiles());

        System.out.println("---------------------------");
        System.out.println("---------------------------");
        System.out.println("---------------------------");
        System.out.println("---------------------------");
        System.out.println("---------------------------");
        System.out.println("查询到的文件夹 :");

        listDir.forEach(p -> {

            if(!p.isFile()){

                System.out.println(p.getName());
            }
        });

        System.out.println("请输入文件夹名  确定文件夹直接回车");

        String next = new Scanner(System.in).nextLine();

        if(next != null && !next.equals("")){

            for(File f2 : listDir){

                if(!f2.isFile()){

                    if(f2.getName().toLowerCase().contains(next.toLowerCase())){

                        select(f2);

                        return;
                    }
                }
            }

        } else {

            lastFile = f;
        }
    }

    /**
     * 三方方法
     */
    private interface MyUser32 extends StdCallLibrary {

        MyUser32 INSTANCE = (MyUser32) Native.loadLibrary("user32", MyUser32.class);
        boolean SystemParametersInfoA(int uiAction, int uiParam, String fnm, int fWinIni);
    }

    /**
     * 修改壁纸的图片路径
     *
     * @param img
     */
    public static void change(String img){
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "Wallpaper", img);
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "WallpaperStyle", "10");
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,
                "Control Panel\\Desktop", "TileWallpaper", "0");
        MyUser32.INSTANCE.SystemParametersInfoA(0x14, 0,
                img, 0x01 | 0x02 );
    }




}