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
 */
public class uartIntercom extends Intercom{
    protected String command = "";
    protected String message = "";
    private final static String[] dev_list = {"/dev/intercom_A1840","/dev/SA808"};
    private final static String AT = "AT";
    private final static String DMO = "+DMO";
    private final static String CONNECT = "CONNECT";
    private final static String MSG = "MSG";
    private final static String VOLUME = "SETVOLUME=";
    private final static String GRP = "SETGROUP=0,";
    private final static int INTERCOM_PULL_DOWN = 0;
    private final static int INTERCOM_PULL_UP =  1;
    //private final static int INTERCOM_SPEAKER_MODE = 2;
    private final static int INTERCOM_HEADSET_MODE = 3;
    private final static int INTERCOM_SPEAKER_MODE = 4;
    SerialPortFinder serialPortFinder = new SerialPortFinder();
    SerialPort uart;
    public String port = "/dev/ttyMT1";
    public String ctl = "";
    public Integer baud = 9600;
    private Integer Volume = 6;
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
        return 0;
    }
    @Override
    public void closeCharDev()
    {
        //
    }
    @Override
    public int getIntercomVersion()
    {
        return 2014;
    }
    @Override
    public String getMessage()
    {
        return "";
    }
    @Override
    public void intercomHeadsetMode()
    {
        ioctl(ctl,INTERCOM_HEADSET_MODE);
    }
    @Override
    public void intercomPowerOff()
    {
        ioctl(ctl,INTERCOM_PULL_DOWN);
        Log.e("UART","Powered OFF");
        if(uart != null){
            uart.close();
            uart = null;
        }
    }
    @Override
    public void intercomPowerOn()
    {
        ioctl(ctl,INTERCOM_PULL_UP);
        Log.e("UART","Powered ONN");
        if(uart == null){
            try {
                uart = new SerialPort(new File(port),baud,0);
                uart.getOutputStream().write((AT+DMO+CONNECT+"\r\n").getBytes("US-ASCII"));
            } catch (IOException e) {
                e.printStackTrace();
                uart = null;
            }
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
        return 0;
    }
    @Override
    public void setCtcss(int paramInt)
    {
        RxCTCSS = paramInt;
        sendFreq();
    }
    @Override
    public void setRXFrequency(int paramInt)
    {
        RxFreq = paramInt;
        sendFreq();
    }
    @Override
    public void setRadioFrequency(int paramInt)
    {
        RxFreq = paramInt;
        sendFreq();
    }
    @Override
    public void setSq(int paramInt)
    {
        SQ = paramInt;
        sendFreq();
    }
    @Override
    public void setTXFrequency(int paramInt)
    {
        TxFreq = paramInt;
        sendFreq();
    }
    @Override
    public void setTxCtcss(int paramInt)
    {
        TxCTCSS = paramInt;
        sendFreq();
    }
    @Override
    public void setVolume(int paramInt)
    {
        Volume = paramInt;
        sendVol();
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
                    ((TxFreq<10000000)?Double.toString(TxFreq/10000):Double.toString(TxFreq/100000))+","+
                    ((RxFreq<10000000)?Double.toString(RxFreq/10000):Double.toString(RxFreq/100000))+","+
                    RxCTCSS+","+SQ+","+TxCTCSS+"\r\n";
            Log.w("WRITE SendFREQ",str);
            try {
                uart.getInputStream().reset();
                uart.getOutputStream().write(str.getBytes("US-ASCII"));
                //uart.getInputStream().
                Thread.sleep(200L);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void sendVol(){
        if(uart != null){
            String str = AT+DMO+VOLUME+Volume+"\r\n";
            Log.w("WRITE SendVOL",str);
            try {
                uart.getOutputStream().write(str.getBytes("US-ASCII"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class SerialPort {

        private static final String TAG = "SerialPort";

        /*
         * Do not remove or rename the field mFd: it is used by native method close();
         */
        private File mFd;
        private FileInputStream mFileInputStream;
        private FileOutputStream mFileOutputStream;

        public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {

                /* Check access permission */
            if (!device.canRead() || !device.canWrite()) {
                try {
                                /* Missing read/write permission, trying to chmod the file */
                    Process su;
                    su = Runtime.getRuntime().exec("/system/bin/su");
                    String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
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

            mFd = open(device.getAbsolutePath(), baudrate, flags);
            if (mFd == null) {
                Log.e(TAG, "native open returns null");
                throw new IOException();
            }
            mFileInputStream = new FileInputStream(mFd);
            mFileOutputStream = new FileOutputStream(mFd);
        }

        // Getters and setters
        public InputStream getInputStream() {
            return mFileInputStream;
        }

        public OutputStream getOutputStream() {
            return mFileOutputStream;
        }

        private static File open(String path, int baudrate, int flags){
            try {
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/sh");
                String cmd = "stty -F " + path +" "+ Integer.toString(baudrate) + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
            return new File(path);
        }
        public void close(){

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
