package com.nxn.intercomm;

/**
 * Created by NeiroN on 20.12.13.
 */

import java.io.*;
import java.util.Iterator;
import java.util.Vector;

import android.hardware.Intercom;
import android.util.Log;
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
Описание Эта команда указывает параметры информационных установки модуля. Формат AT +DMOSETGROUP= GBW, TFV, RFV, CXCSS, SQ
Пример AT +DMOSETGROUP= 0,415.1250,415.1250,12,4
+DMOSETGROUP: 0 Параметры сказал Ясно
GBW: параметры полосы пропускания.
0: 12.5K 1: 25K
TFV: частота передачи. (400.0000M-470.0000M)(Потребность 12.5K И 25K Из Целое несколько)
RFV: частота приема.(400.0000M-470.0000M)
CXCSS: CTCSS Значение.(00-38)
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
public class uartIntercom extends Intercom{
    protected String command = "";
    protected String message = "";
    private final static String[] dev_list = {"/dev/intercom_A1840","/dev/SA808"};
    private final static String AT = "AT";
    private final static String DMO = "+DMO";
    private final static String VERQ = "VERQ";
    private final static String CONNECT = "CONNECT";
    private final static String MES = "MES=";
    private final static String VOLUME = "SETVOLUME=";
    private final static String MIC = "SETMIC=";
    private final static String VOX = "SETVOX=";
    private final static String GRP = "SETGROUP=0,";
    private final static int INTERCOM_PULL_DOWN = 0;
    private final static int INTERCOM_PULL_UP =  1;
    //private final static int INTERCOM_SPEAKER_MODE = 2;
    private final static int INTERCOM_HEADSET_MODE = 3;
    private final static int INTERCOM_SPEAKER_MODE = 4;
    SerialPortFinder serialPortFinder = new SerialPortFinder();
    SerialPort uart = null;
    public String port = "/dev/ttyMT1";
    public String ctl = "";
    public Integer baud = 9600;
    public Integer Ver = 2014;
    private Integer Volume = 6;
    private Integer Mic = 5;
    private Integer Scram = 0;
    private Integer Tot = 0;
    private Integer Vox = 0;
    private Integer SQ = 5;
    private Integer RxFreq = 4460062;
    private Integer TxFreq = 4460062;
    private Integer RxCTCSS = 0;
    private Integer TxCTCSS = 0;

    public uartIntercom(){
        //String[] ports = serialPortFinder.getAllDevices();
        //String[] ports_path = serialPortFinder.getAllDevicesPath();
        Log.e("UART","Init");
        /**
         * TODO Find Intercom and get version
         */
        for(String d:dev_list){
            File dev1 = new File(d);
            if(dev1.exists()) ctl = d;
        }
        Log.e("UART","Control: "+ctl);
    }
    @Override
    public int checkMessageBuffer()
    {
        return 10;
    }
    @Override
    public void closeCharDev()
    {
        //
    }
    @Override
    public int getIntercomVersion()
    {
        /*if(uart != null){
            uart.write(AT+DMO+VERQ);
            try {
                //String line = uart.readLine();
                //if(line == null) return Ver;
                //if(line.contains("80BK"))Ver=1;
                //if(line.contains("81BK"))Ver=2;
                //if(line.contains("D150"))Ver=3;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        return Ver;
    }
    @Override
    public String getMessage()
    {
        String line = uart.readLine();
        if(line == null)return null;
        if(line.contains(DMO+MES+"="))
            return line.replace(DMO+MES+"=","");
        else if(line.contains(VERQ)){
            if(line.contains("80BK"))Ver=1;
            if(line.contains("81BK"))Ver=2;
            if(line.contains("D150"))Ver=3;
        }
        return line;
    }
    @Override
    public void intercomHeadsetMode()
    {
        ioctl(ctl,INTERCOM_HEADSET_MODE);
    }
    @Override
    public void intercomPowerOff()
    {
        try {
            ioctl(ctl,INTERCOM_PULL_DOWN);
            Log.e("UART","Powered OFF");
            uart.close();
            uart = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void intercomPowerOn()
    {
        try {
            ioctl(ctl,INTERCOM_PULL_UP);
            Log.e("UART","Powered ONN");
            Thread.sleep(500L);
            uart = new SerialPort(new File(port),baud,0);
            uart.write(AT+DMO+VERQ);
            sendMic();
        } catch (Exception e) {
            e.printStackTrace();
            uart = null;
        }
    }
    @Override
    public void intercomSpeakerMode()
    {
        ioctl(ctl,INTERCOM_SPEAKER_MODE);
    }
    @Override
    public void openCharDev()
    {
        //
    }
    @Override
    public void resumeIntercomSetting()
    {
        sendFreq();
        //JNI_resumeIntercomSetting();
    }
    @Override
    public int sendMessage(String paramString)
    {
        uart.write(AT+DMO+MES+"="+paramString);
        return 0;
    }
    @Override
    public void setCtcss(int paramInt)
    {
        if(RxCTCSS != paramInt){
            RxCTCSS = paramInt;
            sendFreq();
        }
    }
    @Override
    public void setRXFrequency(int paramInt)
    {
        if(RxFreq != paramInt){
            RxFreq = paramInt;
            sendFreq();
        }
    }
    @Override
    public void setRadioFrequency(int paramInt)
    {
        if(RxFreq != paramInt){
            RxFreq = paramInt;
            sendFreq();
        }
    }
    @Override
    public void setSq(int paramInt)
    {
        if(SQ != paramInt){
            SQ = paramInt;
            sendFreq();
        }
    }
    @Override
    public void setTXFrequency(int paramInt)
    {
        if(TxFreq != paramInt){
            TxFreq = paramInt;
            sendFreq();
        }
    }
    @Override
    public void setTxCtcss(int paramInt)
    {
        if(TxCTCSS != paramInt){
            TxCTCSS = paramInt;
            sendFreq();
        }
    }
    @Override
    public void setVolume(int paramInt)
    {
        if(Volume != paramInt){
            Volume = paramInt;
            sendVol();
        }
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
    private void ioctl(String dev, int param){
        try {
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/sh");
                String cmd = "ioctl " + dev +" "+ Integer.toString(param) + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
        } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
        }
    }

    private void sendFreq(){
        if(uart != null){
            String str = AT+DMO+GRP+
                    ((TxFreq<10000000)?Double.toString(Double.valueOf(TxFreq)/10000):Double.toString(Double.valueOf(TxFreq)/100000))+","+
                    ((RxFreq<10000000)?Double.toString(Double.valueOf(RxFreq)/10000):Double.toString(Double.valueOf(RxFreq)/100000))+","+
                    String.format("%02d,%d,%02d",RxCTCSS,SQ,TxCTCSS);
            uart.write(str);
        }
    }
    private void sendVol(){
        if(uart != null){
            uart.write(AT+DMO+VOLUME+Volume.toString());
        }
    }
    private void sendMic(){
        if(uart != null){
            uart.write(AT+DMO+MIC+String.format("%d,%d,%d",Mic,Scram,Tot));
        }
    }
    private void sendVox(){
        if(uart != null){
            uart.write(AT+DMO+VOX+Vox.toString());
        }
    }
    public static class SerialPort {

        private static final String TAG = "SerialPort";

        private File mFd;
        private Process sin;
        private Process sout;
        private BufferedReader br;

        public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
                /* Check access permission */
            if (!device.canRead() || !device.canWrite()) {
                try {
                                /* Missing read/write permission, trying to chmod the file */
                    Process su;
                    su = Runtime.getRuntime().exec("/system/bin/su");
                    String cmd = "chmod 777 " + device.getAbsolutePath() + "\n"
                            + "exit\n";
                    su.getOutputStream().write(cmd.getBytes());
                    if ((su.waitFor() != 0) || !device.canRead()
                            || !device.canWrite()) {
                        throw new SecurityException();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new SecurityException();
                }
            }
            sin = Runtime.getRuntime().exec("/system/bin/sh");
            try {
                String cmd = "stty -F " + device.getAbsolutePath() +" "+ Integer.toString(baudrate) + " sane\n";
                sin.getOutputStream().write(cmd.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
            mFd = device;
            if (mFd == null) {
                Log.e(TAG, "native open returns null");
                throw new IOException();
            }
            sout = Runtime.getRuntime().exec("/system/bin/cat "+device.getAbsolutePath());
            br = new BufferedReader(new InputStreamReader(sout.getInputStream()));
        }
        public void close(){
            sin.destroy();
            sout.destroy();
        }
        // Getters and setters
        public String readLine() {
            try {
                String line = null;
                if(br.ready())
                    line = br.readLine();
                if(line != null && line.contains(DMO))
                    return line;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        public void write(String line){
            try {
                String cmd = "echo \""+ line +"\" >> " + mFd.getAbsolutePath() +"\n";
                Log.e(TAG, cmd);
                sin.getOutputStream().write(cmd.getBytes());
                Thread.sleep(500L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

   }

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
