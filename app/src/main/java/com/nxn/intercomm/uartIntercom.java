package com.nxn.intercomm;

/**
 * Created by NeiroN on 20.12.13.
 */

import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;
/*
Протокол последовательной связи
1 Контур
A1841 Модуль AT командного интерфейса, вы можете легко общаться и контролировать модуль через эти АТ команд. Модуль обеспечивает AT команды, установленного для данного модуля охватывает все запросы и команды управления, Производители в использовании в соответствии с их потребностями, Поведение Выберите в использовании. 1.1 AT Инструкция Тип
Потому что AT Директива как интерфейс, так что это возвращаемые значения команд и форматов фиксированы, вся Сказать AT Есть четыре формы обучения:
1, параметры не командовать: Простой Формат команды: AT + <command>, таких как: AT + DMOCONNECT 2, с параметрами команды: чаще формате, он предлагает большую гибкость Формат команды следующий:
AT + <command> = <par1>, <par2>, <par3> ...
Эта команда возвращает то же самое в зависимости от типа обучения, который будет объяснить конкретные указания, приведенные в спину, Но вернемся Основная структура для формата возвращаемого значения: <cr> <lf> <response String> <CR> <LF> <CR> Возврата каретки, 0x0D <LF> Wrap, 0x0A. 1.2 AT Формат команды
Ниже приведен поддержку команды в и возврата инструкции: В формате командной:
АТ команд начинают с "AT" в <CR> конца.После того, как модуль работает, серийные установки по умолчанию:8 бит данных,1 стоп-бит, без контроля четности, аппаратное управление потоком (CTS / RTS), скорость9600 AT команды возвращаются формат:
<CR> <LF> <Associated С командой AT String> <CR> <LF>
Примечание введите команду AT, ни эха выход.
2 Интерфейс связи Формат команды
2.1.1 Определение формата кадра
Использует протокол связи AT Инструкции связью.(Примечание, все переводы ASCII Коробка передач код) Конец в модуле связи в виде: AT + DMOXXX
Терминальный модуль связи в виде: + DMOXXX (клемма без возврата)
2.2 Команда интерактивный процесс
2.2.1 AT + DMOCONNECT Рукопожатие сигнализации
Описание
Рукопожатие сигнализации модуль призван продемонстрировать нормальную работу, Терминал Направлено раз каждого модуля в получении этого письма После столь Ответов сигнализации реагирования; например3Ответ модуль Рукопожатие сигнализация не получил, повторном запуске терминала Модуль.
Формат
AT + DMOCONNECT
Пример AT + DMOCONNECT
+DMOCONNECT: 0
2.2.2 + DMOCONNECT ответы рукопожатие сигнализации
Описание Ответ Рукопожатие модуль сигнализации, модуль должен быть получен DMOCONNECT После сигнализации, первый ответ время.
Формат +DMOCONNECT: 0
Пример +DMOCONNECT: 0
Параметр Описание 0 нормальном рабочем состоянии
2.2.3 AT + DMOSETGROUP Набор команд
Описание Эта команда указывает параметры информационных установки модуля. Формат AT +DMOSETGROUP= GBW, TFV, RFV, RXCTCSS, SQ, TXCTCSS
Пример AT +DMOSETGROUP= 0,415.1250,415.1250,12,4,12
+DMOSETGROUP: 0 Параметры сказал Ясно
GBW: параметры полосы пропускания.
     0: 6.5K
     1: 5K
TFV: частота передачи. (400.0000M-470.0000M)
RFV: частота приема.(400.0000M-470.0000M)
RXCTCSS: принимающий тон (00-121)
TXCTCSS: передающий тон (00-121)
    CTCSS Значение.(00-38)
    CDCSS значение (39-121)
Примечание: частота передачи и частота приема могут быть одинаковыми или отличными частоты, общий То же самое CTCSS не 00: Нет Кодировка 01-38: CTCSS
SQ: уровень шумоподавления (0-8)
0: Вы не можете использовать режим сканирования: режим (Примечание прослушивания 0)

2.2.4 + DMOSETGROUP Ответ команды Set Group
Описание После того, как модуль получает команду, чтобы установить группу, чтобы вернуться к операционной результате
Формат + DMOSETGROUP: Х
Пример + DMOSETGROUP: 0
Параметр Описание Х: 0 Успешное
Набор данных вне диапазона
2.2.5 AT + DMOAUTOPOWCONTR Функция автоматического энергосбережения установить команду
Описание Установите функцию автоматического энергосбережения модуля.
Формат AT + DMOAUTOPOWCONTR = Х
Пример AT + DMOAUTOPOWCONTR = 0
+ DMOAUTOPOWCONTR: 0
Параметр Описание Х: 0 Комплект сохранения открытых , 1 Выключите настройки энергосбережения

2.2.6 + DMOAUTOPOWCONTR Автоматического энергосбережения установлено ответ
Описание Автоматические настройки модуля питания привести реакцию
Формат + DMOAUTOPOWCONTR: X
Пример + DMOAUTOPOWCONTR: 0
Параметр Описание Х: 0 открытым Запрет
2.2.7 AT + DMOVERQ параметров модуля запроса
Описание Параметры командной Запрос модуль
Формат AT+DMOVERQ
Пример AT+DMOVERQ
+DMOVERQ: V1.1
Параметр Описание

2.2.8 + DMOVERQ Дискретизации в режиме Запрос
Описание
После того, как модуль получает запрос командные параметры модуля, вернитесь в операционную результате
Формат + DMOVERQ: X
Пример + DMOVERQ: V1.1
Параметр Описание X: номер версии программного обеспечения (V1.0)
2.2.9 AT + DMOSETVOLUME Установка уровня громкости
Описание: Установите модули уровня громкости
Формат: AT + DMOSETVOLUME = Х
Пример: AT + DMOSETVOLUME = 1
+ DMOSETVOLUME:0
Параметр Описание X: Уровень громкости параметр 1-6 Уровень
2.2.10 + DMOSETVOLUME Ответ настройка громкости
Описание: Результаты установить уровень громкости модуля, передавшего ответное
Формат: + DMOSETVOLUME:X
Пример + DMOSETVOLUME:0
Параметр Описание X: 0 Успешно установить, 1 Установите провал
2.2.11 Установите контроллер для отправки текстовых сообщений с командным модулем
Описание:
Мобильные телефоны для отправки текстовых сообщений с модулем
Формат Телефон для отправки СМС команду: AT + DMOMES = (Сообщение Протяженность) XXXX
Модули принять команду ответное подтверждение СМС: + DMOMES:0
Пример: AT + DMOMES = Я в Шэньчжэнь
+ DMOMES: 0 Параметр Описание XXXX -Содержание SMS.
+ DMOMES: 0 После приема текстового сообщения, чтобы подтвердить команду модуль и передается на мобильный телефон ответ.
2.2.12 модуль для отправки команд SMS с настройками контроллера
Описание
Получать сообщения, отправленные с модулем мобильного телефона
Формат Модуль для отправки текстовых сообщений на мобильные телефоны командовать:+ DMOMES = (Сообщение Протяженность) XXXX Мобильный SMS ответ получил команду подтверждения: AT + DMOMES:0
Пример + DMOMES = Я в Шэньчжэнь
AT + DMOMES:0 Параметр Описание XXXX Содержание SMS.
AT + DMOMES: 0 Ответить на сообщение о подтверждении после получения модуль для мобильных телефонов.

41 54 2B 44 4D 4F 4D 45 53 3D 1E 3D 3E 3F 40 41 42 43 44 45 46 47 48 49 4A 4B 4C 4D 4E 4F 50 51 52 53 54 55 56 57 58 59 5A


 AT+DMOSETMIC= MICVL,SCRAMLVL,TOT
 MICVL: 1..8
 SCRAMLVL:0..8
 TOT:0..

AT+DMOSETVOX=[0..8]
0: OFF
1:12 mV
5: 7 mV
8: 5 mV



 */
public class uartIntercom{
    native int OpenSerial(String dev, int baud);
    native int CloseSerial();
    native String SerialRead();
    native int SerialWrite(String str);
    native int Ioctl(String dev,long cmd, long param);
    static {
        System.loadLibrary("Serial");
    }
    private final static String[][] dev = {
            {
                    "DevName",// /system/build.prop  ro.product.device
                    "Human readable name",
                    "/dev/port",
                    "/dev/Special_dev",
                    "echo \"Power ON command\"",
                    "echo \"Power OFF command\"",
                    "echo \"Speaker MODE\"",
                    "echo \"HeadSet MODE\"",
                    "echo \"TX Button ON\"",
                    "echo \"TX Button OFF\"",
                    "Module Version",
                    "CONNECT,VERQ,SETGROUP,SETVOLUME,SETMIC,SETVOX,MES,PTT_EINT,cmd0,cmd1,cmd2,cmd3"
            },{
            "RunboX5",
            "Runbo X5-W UHF",
            "/dev/ttyMT1",
            "/dev/intercom_A1840",
            "ioctl /dev/intercom_A1840 1",
            //"echo \"-w=119: 0 0 1 1 1 1 0\" > /sys/class/misc/mtgpio/pin\n" +
            //        "echo \"-w=125: 0 0 1 1 1 1 0\" > /sys/class/misc/mtgpio/pin\n" +
            //        "echo \"-w=182: 0 0 1 1 1 1 0\" > /sys/class/misc/mtgpio/pin\n",
            "ioctl /dev/intercom_A1840 0",
            //"echo \"-w=119: 0 0 0 0 1 1 0\" > /sys/class/misc/mtgpio/pin\n" +
            //        "echo \"-w=182: 0 0 0 0 1 1 0\" > /sys/class/misc/mtgpio/pin\n",
            "ioctl /dev/intercom_A1840 4",
            //"echo \"-w=122: 0 0 0 0 1 1 0\" > /sys/class/misc/mtgpio/pin\n" +
            //        "echo \"-w=125: 0 0 1 1 1 1 0\" > /sys/class/misc/mtgpio/pin\n",
            "ioctl /dev/intercom_A1840 3",
            //"echo \"-w=122: 0 0 1 1 1 1 0\" > /sys/class/misc/mtgpio/pin\n" +
            //        "echo \"-w=125: 0 0 0 0 1 1 0\" > /sys/class/misc/mtgpio/pin\n",
            "su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout12 1\" > $g;'",
            "su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout12 0\" > $g;'",
            "HKT-81BK",
            "true,true,true,true,true,true,true,false,false,false,false,false"
    },{
            "G26",
            "Runbo X5-W VHF",
            "/dev/ttyMT1",
            "/dev/intercom_A1840",
            "ioctl /dev/intercom_A1840 1",
            "ioctl /dev/intercom_A1840 0",
            "ioctl /dev/intercom_A1840 4",
            "ioctl /dev/intercom_A1840 3",
            "su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout12 1\" > $g;'",
            "su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout12 0\" > $g;'",
            "HKT-D150",
            "true,true,true,true,true,true,true,false,false,false,false,false"
    },{
            "hexing89_we_jb2",
            "Runbo Q5(X6)",
            "/dev/ttyMT3",
            "/dev/intercom_A1840",
            "ioctl /dev/intercom_A1840 1",
            "ioctl /dev/intercom_A1840 0",
            "ioctl /dev/intercom_A1840 4",
            "ioctl /dev/intercom_A1840 3",
            "su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout12 1\" > $g;'",
            "su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout12 0\" > $g;'",
            "HKT-81BK",
            "true,true,true,true,true,true,true,false,false,false,false,false"
    },{
            "M8",
            "Snowpow M8",
            "/dev/ttyMT1",
            "/dev/a1852",
            //"su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout73 1\" > $g; echo \"-wdout116 1\" > $g; echo \"-wdout126 1\" > $g;'",
            "ioctl /dev/a1852 -0x3ffbb2ff",
            //"su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout73 0\" > $g; echo \"-wdout116 0\" > $g; echo \"-wdout126 0\" > $g;'",
            "ioctl /dev/a1852 -0x3ffbb2fe",
            //"su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout125 1\" > $g; echo \"-wdout182 0\" > $g;'",
            "echo \"ioctl /dev/a1852 -0x3ffbb2fb\"",
            //"su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout125 0\" > $g; echo \"-wdout182 1\" > $g;'",
            "echo \"ioctl /dev/a1852 -0x3ffbb2fa\"",
            "ioctl /dev/a1852 -0x3ffbb2fd",
            "ioctl /dev/a1852 -0x3ffbb2fc",
            "A1842",
            "true,true,true,true,false,false,true,false,false,false,false,false"
    },{
            "M9",
            "Snowpow M9",
            "/dev/ttyMT1",
            "/dev/a1852",
            "ioctl /dev/a1852 -0x3ffbb2ff",
            "ioctl /dev/a1852 -0x3ffbb2fe",
            "echo \"ioctl /dev/a1852 -0x3ffbb2fb\"",
            "echo \"ioctl /dev/a1852 -0x3ffbb2fa\"",
            "ioctl /dev/a1852 -0x3ffbb2fd",
            "ioctl /dev/a1852 -0x3ffbb2fc",
            "A1842",
            "true,true,true,true,false,false,true,false,false,false,false,false"
    },{
            "mbk89_wet_jb2",
            "Batl S19",
            "/dev/ttyMT1",
            "/dev/null",
            "su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout178 1\" > $g; echo \"-wdout179 1\" > $g; echo \"-wdout177 1\" > $g;'",
            "su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout75 0\" > $g;echo \"-wdout76 0\" > $g; echo \"-wdout174 0\" > $g; echo \"-wdout173 0\" > $g; echo \"-wdout179 0\" > $g;'",
            "su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout75 1\" > $g; echo \"-wdout174 1\" > $g; echo \"-wdout173 0\" > $g; echo \"-wdout76 0\" > $g;'",
            "su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout75 0\" > $g; echo \"-wdout174 0\" > $g; echo \"-wdout173 1\" > $g; echo \"-wdout76 1\" > $g;'",
            "su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout178 0\" > $g;'",
            "su -c 'g=\"/sys/class/misc/mtgpio/pin\";echo \"-wdout178 1\" > $g;'",
            "SA808",
            "true,false,true,true,false,false,true,true,false,false,false,false"
    },{
            "bd79_89_w_emmc_sxtd_w63",
            "Batl S09/W63",
            "/dev/ttyMT1",
            "/dev/SA808",
            "echo \"A\" > /dev/SA808",
            "echo \"B\" > /dev/SA808",
            "echo \"None\" ",
            "echo \"None\" ",
            "echo \"No Button\"",
            "echo \"No Button\"",
            "SA808",
            "true,false,true,true,false,false,true,false,false,false,false,false"
    }//{"/dev/intercom_A1840","/dev/SA808","/dev/a1852"};
    };
    private final static String[][] module = {
            {"Name","superID","minFreq","MaxFreq","maxVol","micVol","maxCt","txCt"},
            {"HKT-80BK","80BK","400.0","480.0","8","5","120","true"},
            {"HKT-81BK","81BK","400.0","480.0","8","5","120","true"},
            {"HKT-D150","D150","130.0","180.0","8","5","120","true"},
            {"SA808","ERR","400.0","480.0","8","0","38","true"},
            {"A1842","1842","400.0","480.0","8","0","38","false"}
    };
    private final static String AT = "AT";
    private final static String DMO = "+DMO";
    private final static String VERQ = "VERQ";
    private final static String CONNECT = "CONNECT";
    private final static String MES = "MES=";
    private final static String VOLUME = "SETVOLUME=";
    private final static String MIC = "SETMIC=";
    private final static String VOX = "SETVOX=";
    private final static String GRP = "SETGROUP=";
    private final static String CR = "\r\n";
    private static final String FORMAT = "###.####";
    private NumberFormat Format;
    private String modelName = "";
    private String devPath = "";
    private String powerOn = "";
    private String powerOff = "";
    private String speakerMode = "";
    private String headsetMode = "";
    private String txOn = "";
    private String txOff = "";
    private String dBuf =  "";
    private Boolean Debug = false;
    private String[] Profile = {};
    private Boolean isPtt = false;
    private int uart = -1;
    private String Ver = "";
    private Integer Volume = 6;
    private Integer Mic = 0;
    private Integer Scram = 0;
    private Integer Tot = 0;
    private Integer Vox = 0;
    private Integer SQ = 5;
    private Double maxFreq = 480.0;
    private Double minFreq = 400.0;
    private Integer maxVol = 8;
    private Integer maxCt = 120;
    private Boolean txCt = true;
    private Integer Gwb = 0;
    private Double RxFreq = 450.0500;
    private Double TxFreq = 450.0500;
    private Integer RxCTCSS = 0;
    private Integer TxCTCSS = 0;
    private String[] ports = {};
    private String port = "/dev/null";
    private Process shell;

    public uartIntercom(String config, String _port, String _def_ver, Boolean debug){
        try {
            shell = Runtime.getRuntime().exec("/system/bin/sh");
        } catch (IOException e) {
            e.printStackTrace();
            _def_ver = "no shell!";
        }
        Debug = debug;
        Ver = _def_ver;
        port = _port;
        ports = new SerialPortFinder().getAllDevicesPath();
        Log.d("UART","Init");
        if(!config.equals("") && config.split(";").length == 15){
            String[] d = config.split(";");
            modelName = d[0];
            //Arrays.asList(ports).contains(d[1]);
            port = d[1];
            devPath = d[2];
            powerOn = d[3];
            powerOff = d[4];
            speakerMode = d[5];
            headsetMode = d[6];
            txOn = d[7];
            txOff = d[8];
            Ver = d[9];
            minFreq = Double.parseDouble(d[10]);
            maxFreq = Double.parseDouble(d[11]);
            maxVol = Integer.parseInt(d[12]);
            maxCt = Integer.parseInt(d[13]);
            txCt = Boolean.parseBoolean(d[14]);
            Profile = d[15].split(",");
        }else
        for(String[] d:dev)
            if(Build.DEVICE.contains(d[0])){
                modelName = d[1];
                port = d[2];
                devPath = d[3];
                powerOn = d[4];
                powerOff = d[5];
                speakerMode = d[6];
                headsetMode = d[7];
                txOn = d[8];
                txOff = d[9];
                Ver=d[10];
                Profile=d[11].split(",");
            }
        //Create format
        Format = NumberFormat.getInstance(Locale.ENGLISH);
        ((DecimalFormat)Format).applyPattern(FORMAT);
        Format.setMinimumFractionDigits(FORMAT.length() - FORMAT.indexOf(".")-1);
        Format.setMinimumIntegerDigits(FORMAT.indexOf("."));
    }

    public Double getMaxFreq(){
        return maxFreq;
    }

    public Double getMinFreq(){
        return minFreq;
    }

    public Integer getMaxCt(){
        return maxCt;
    }

    public String[] getPorts(){
        return ports;
    }
    public String getModel(){
        return modelName;
    }
    public String getConfig(){
        String out = "";
        out += modelName + "\n";
        out += port + "\n";
        out += devPath + "\n";
        out += powerOn + "\n";
        out += powerOff + "\n";
        out += speakerMode + "\n";
        out += headsetMode + "\n";
        out += txOn + "\n";
        out += txOff + "\n";
        out += Ver + "\n";
        out += Double.toString(minFreq) + "\n";
        out += Double.toString(maxFreq) + "\n";
        out += Integer.toString(maxVol) + "\n";
        out += Integer.toString(maxCt) + "\n";
        out += Boolean.toString(txCt) + "\n";
        out += MainActivity.join(Profile,",")+"\n(auto)";
        return out;
    }
    public Boolean getProfile(int id){
        return id < Profile.length && Boolean.parseBoolean(Profile[id]);
    }

    public void setProfile(Boolean value,int id){
        if(id < Profile.length)Profile[id]=value.toString();
    }

    public void setPort(String str){
        if(!port.equals(str)){
            port = str;
            uart = OpenSerial(port,9600);
        }
        /*try{

            uart = new SerialPort(new File(port),9600);
        }catch (IOException e){
            e.printStackTrace();
        }*/
    }

    public Boolean checkMessageBuffer()
    {
        if(uart > 0){
            return true;//uart.checkBuffer();
        }else{
            return false;
        }
    }
    private Double checkFreq(Double param){
        if(param > maxFreq) param = maxFreq;
        if(param < minFreq) param = minFreq;
        return param;
    }
    private Integer checkCt(Integer param){
        if(param > maxCt)param = maxCt;
        return param;
    }
    private String getModule(String param){
        for(String[] m:module){
            if(param.contains(m[1]) || param.equals(m[0])){
                minFreq = Double.parseDouble(m[2]);
                maxFreq = Double.parseDouble(m[3]);
                maxVol = Integer.parseInt(m[4]);
                maxCt = Integer.parseInt(m[6]);
                txCt = Boolean.parseBoolean(m[7]);
                return m[0];
            }
        }
        return param.replace(DMO+VERQ+":","");
    }

    public String getIntercomVersion()
    {
        if(!getProfile(1))return getModule(Ver);
        if(uart > 0){
            SerialWrite(AT+DMO+VERQ+CR);
            //uart.write(AT+DMO+VERQ);
            try {
                String line;
                while ((line = SerialRead())==null){Thread.sleep(200L);Log.w("UART","sleep(200L)");} //wait for module respond
                if(!line.contains(DMO+VERQ)) return Ver;
                Ver=getModule(line);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Ver;
    }

    public String getMessage()
    {
        if(!getProfile(6))return null;
        String line = SerialRead();
        if(line == null)return null;
        if(line.contains(DMO+MES))
            return line.replace(DMO+MES,"");
        else if(line.contains(VERQ))Ver=getModule(line);
        return null;
    }

    public void intercomPttTogle()
    {
        if(isPtt){
            cmd(txOff);
            isPtt = false;
        }else{
            cmd(txOn);
            isPtt = true;
        }
    }

    public void intercomTxOn()
    {
        cmd(txOn);
    }
    public void intercomTxOff()
    {
        cmd(txOff);
    }

    public void intercomHeadsetMode()
    {
        cmd(headsetMode);
    }

    public void intercomPowerOff()
    {
        try {
            cmd(powerOff);
            Log.w("UART", "Powered OFF");
            //uart.close();
            CloseSerial();
            uart = -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void intercomPowerOn()
    {
        try {
            CloseSerial();
            uart = OpenSerial(port,9600);//new SerialPort(new File(port), 9600);
            cmd(powerOn);
            Log.w("UART","Powered ONN");
            Thread.sleep(500L);
            if(getProfile(0)){
                SerialWrite(AT + DMO + CONNECT + CR);
                //uart.write(AT + DMO + CONNECT);
                Thread.sleep(200L);
                String line = SerialRead();//uart.readLine();
                if(line.contains(CONNECT))Log.w("UART","Connected OK");
            }
        } catch (Exception e) {
            e.printStackTrace();
            uart = -1;
        }
    }

    public void intercomSpeakerMode()
    {
        cmd(speakerMode);
    }

    public void resumeIntercomSetting()
    {
        if(Mic != 0)sendMic();
        if(RxFreq != 4500500 || TxFreq != 4500500)sendFreq();
        if(Volume != 0)sendVol();
        if(Vox != 0)sendVox();
    }

    public int sendMessage(String paramString)
    {
        if(!getProfile(6))return 0;
        if(paramString.length() < 100){
            SerialWrite(AT + DMO + MES + String.format("\\x%d%s", paramString.length(), paramString)+CR);
            //uart.write(AT + DMO + MES + String.format("\\x%d%s", paramString.length(), paramString));
        }else{//Split Messages by 100 byte
            for(int i=0;i < paramString.length();i+=100){
                SerialWrite(AT + DMO + MES + String.format("\\x%d%s", 100, paramString.substring(i, (paramString.length() - i > 100) ? 100 : paramString.length() - i)) + CR);
                //uart.write(AT+DMO+MES+String.format("\\x%d%s",100,paramString.substring(i,(paramString.length()-i>100)?100:paramString.length()-i)));
            }
        }
        return 0;
    }

    public void setCtcss(int paramInt)
    {
        if(RxCTCSS != paramInt){
            RxCTCSS = checkCt(paramInt);
            sendFreq();
        }
    }

    public void setRXFrequency(Double param)
    {
        if(!RxFreq.equals(param)){
            RxFreq = checkFreq(param);
            sendFreq();
        }
    }

    public void setSq(int paramInt)
    {
        if(SQ != paramInt){
            SQ = paramInt;
            sendFreq();
        }
    }

    public void setTXFrequency(Double param)
    {
        if(!TxFreq.equals(param)){
            TxFreq = checkFreq(param);
            sendFreq();
        }
    }

    public void setTxCtcss(int paramInt)
    {
        if(TxCTCSS != paramInt && txCt){
            TxCTCSS = checkCt(paramInt);
            sendFreq();
        }
    }

    public void setVolume(int paramInt)
    {
        if(Volume != paramInt){
            Volume = paramInt;
            sendVol();
        }
    }
    public void init(Double rx, Double tx, int rxt, int txt, int sq,int mic, int scram, int tot, int vox, int volume){
        RxFreq = checkFreq(rx);
        TxFreq = checkFreq(tx);
        RxCTCSS = checkCt(rxt);
        TxCTCSS = checkCt(txt);
        SQ = sq;
        Mic = mic;
        Scram = scram;
        Tot = tot;
        Vox = vox;
        Volume = volume;
    }

    public void setFreq(Double rx, Double tx, int rxt, int txt, int sq, int gwb){
        Boolean set = false;
        if(!RxFreq.equals(rx) || !TxFreq.equals(tx) || RxCTCSS != rxt || TxCTCSS != txt || SQ != sq || Gwb != gwb)set = true;
        RxFreq = checkFreq(rx);
        TxFreq = checkFreq(tx);
        RxCTCSS = checkCt(rxt);
        TxCTCSS = checkCt(txt);
        SQ = sq;
        Gwb = gwb;
        if(set)sendFreq();
    }
    public void setMic_e(int mic, int scram, int tot){
        Boolean set = false;
        if(Mic != mic || Scram != scram || Tot != tot)set = true;
        Mic = mic;
        Scram = scram;
        Tot = tot;
        if(set)sendMic();
    }
    public void setMic(int paramInt)
    {
        if(Mic != paramInt){
            Mic = paramInt;
            sendMic();
        }
    }
    public void setScram(int paramInt)
    {
        if(Scram != paramInt){
            Scram = paramInt;
            sendMic();
        }
    }
    public void setTot(int paramInt)
    {
        if(Tot != paramInt){
            Tot = paramInt;
            sendMic();
        }
    }
    public void setVox(int paramInt)
    {
        if(Vox != paramInt){
            Vox = paramInt;
            sendVox();
        }
    }
    public void cmd(String cmd){
        /*try {
            try {
                if (shell.exitValue() >= 0) shell = Runtime.getRuntime().exec("/system/bin/sh");
            }catch(IllegalThreadStateException e){
            }
            shell.getOutputStream().write((cmd + "\n").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        if(cmd.contains("ioctl")){
            String params[] = cmd.split(" ");
            if(params.length > 2){
                Ioctl(params[1],Long.parseLong(params[2]),0L);
            }
        }
    }

    private void sendFreq(){
        if(!getProfile(2))return;
        if(uart > 0){
            String str = AT+DMO+GRP+(
                    (txCt)?
                            String.format("%d,%s,%s,%02d,%d,%02d",Gwb,Format.format(TxFreq),Format.format(RxFreq),RxCTCSS,SQ,TxCTCSS)
                            :String.format("%d,%s,%s,%02d,%d",Gwb,Format.format(TxFreq),Format.format(RxFreq),RxCTCSS,SQ));
            SerialWrite(str+CR);
            //uart.write(str);
        }
    }

    private void sendVol(){
        if(!getProfile(3))return;
        if(uart > 0){
            if(Volume > maxVol)Volume=maxVol;
            SerialWrite(AT + DMO + VOLUME + Volume.toString() + CR);
            //uart.write(AT+DMO+VOLUME+Volume.toString());
        }
    }

    private void sendMic(){
        if(!getProfile(4))return;
        if(uart > 0){
            SerialWrite(AT + DMO + MIC + String.format("%d,%d,%d",Mic,Scram,Tot) + CR);
            //uart.write(AT+DMO+MIC+String.format("%d,%d,%d",Mic,Scram,Tot));
        }
    }

    private void sendVox(){
        if(!getProfile(6))return;
        if(uart > 0){
            SerialWrite(AT+DMO+VOX+Vox.toString()+CR);
            //uart.write(AT+DMO+VOX+Vox.toString());
        }
    }

    /*public static class SerialPort {

        private static final String TAG = "SerialPort";
        private File mFd;
        private Process mInput;
        private InputStream mInStream;
        private InputStreamReader mInStreamReader;
        private BufferedReader mReader;

        public SerialPort(File device, int baudrate) throws SecurityException, IOException {
            mInput = Runtime.getRuntime().exec("/system/bin/sh");
            try {
                String cmd = "stty -F " + device.getAbsolutePath() +" "+ Integer.toString(baudrate) + " sane\n";
                mInput.getOutputStream().write(cmd.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
            mFd = device;
            if (mFd == null) {
                Log.e(TAG, "native open returns null");
                throw new IOException();
            }
            mReader = new BufferedReader(mInStreamReader = new InputStreamReader(mInStream = new FileInputStream(mFd)));
            Log.d(TAG, "Opened port: " + device.getAbsolutePath());
        }
        public void close(){
            try {
                mInput.destroy();
                mReader.close();
                mInStreamReader.close();
                mInStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Getters and setters
        public String readLine() {
            try {
                String line = null;
                if(mReader.ready())
                    line = mReader.readLine();
                if(line != null && line.contains(DMO)){
                    Log.d(TAG,"<<< "+line);
                    return line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        public void write(String line){
            try {
                Log.d(TAG,">>> "+line);
                String cmd = "echo \""+ line +"\\x0A\" > " + mFd.getAbsolutePath() +"\n";// \x0A for send <CR>. <LF> sended by echo
                mInput.getOutputStream().write(cmd.getBytes());
                Thread.sleep(100L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public Boolean checkBuffer(){
            try {
                return mReader.ready();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
   }*/

    public class SerialPortFinder {

        public class Driver {
            public Driver(String name, String root) {
                mDriverName = name;
                mDeviceRoot = root;
            }
            private String mDriverName;
            private String mDeviceRoot;
            Vector<File> mDevices = null;
            public Vector<File> getDevices() {
                if (mDevices == null) {
                    mDevices = new Vector<File>();
                    File dev = new File("/dev");
                    File[] files = dev.listFiles();
                    int i;
                    for (i=0; i<files.length; i++) {
                        if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
                            Log.d(TAG, "Found new device: " + files[i]);
                            mDevices.add(files[i]);
                        }
                    }
                }
                return mDevices;
            }
            public String getName() {
                return mDriverName;
            }
        }

        private static final String TAG = "SerialPort";

        private Vector<Driver> mDrivers = null;

        Vector<Driver> getDrivers() throws IOException {
            if (mDrivers == null) {
                mDrivers = new Vector<Driver>();
                LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
                String l;
                while((l = r.readLine()) != null) {
                    // Issue 3:
                    // Since driver name may contain spaces, we do not extract driver name with split()
                    String drivername = l.substring(0, 0x15).trim();
                    String[] w = l.split(" +");
                    if ((w.length >= 5) && (w[w.length-1].equals("serial"))) {
                        Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length-4]);
                        mDrivers.add(new Driver(drivername, w[w.length-4]));
                    }
                }
                r.close();
            }
            return mDrivers;
        }

        public String[] getAllDevices() {
            Vector<String> devices = new Vector<String>();
            // Parse each driver
            Iterator<Driver> itdriv;
            try {
                itdriv = getDrivers().iterator();
                while(itdriv.hasNext()) {
                    Driver driver = itdriv.next();
                    Iterator<File> itdev = driver.getDevices().iterator();
                    while(itdev.hasNext()) {
                        String device = itdev.next().getName();
                        String value = String.format("%s (%s)", device, driver.getName());
                        devices.add(value);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return devices.toArray(new String[devices.size()]);
        }

        public String[] getAllDevicesPath() {
            Vector<String> devices = new Vector<String>();
            // Parse each driver
            Iterator<Driver> itdriv;
            try {
                itdriv = getDrivers().iterator();
                while(itdriv.hasNext()) {
                    Driver driver = itdriv.next();
                    Iterator<File> itdev = driver.getDevices().iterator();
                    while(itdev.hasNext()) {
                        String device = itdev.next().getAbsolutePath();
                        devices.add(device);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return devices.toArray(new String[devices.size()]);
        }
    }
}
