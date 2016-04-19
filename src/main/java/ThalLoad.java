
import com.ochafik.lang.jnaerator.runtime.globals.GlobalDouble;
import com.ochafik.lang.jnaerator.runtime.globals.GlobalInt;
import com.sun.jna.*;
import java.util.Arrays;
import java.util.List;
/**
 * Created by Thomas on 2/17/2016.
 */

/**
 * Copyright (C) 2016  Thomas Gregory

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 * JNA Wrapper for library <b>test</b><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 * Depreciated due to new primer design methods.
 *
 * JNA class and interface for Thal code compiled libraries from the Primer3 source code
 *
 * Untergasser A, Cutcutache I, Koressaar T, Ye J, Faircloth BC, Remm M, Rozen SG (2012)
 * Primer3 - new capabilities and interfaces. Nucleic Acids Research 40(15):e115 Koressaar T,
 * Remm M (2007) Enhancements and modifications of primer design program
 * Primer3 Bioinformatics 23(10):1289-91
 */
@Deprecated
public class ThalLoad{
    public static NativeLibrary JNA_NATIVE_LIB;
    public static Thal INSTANCE64;
    public interface Thal extends Library {
        public static final String THAL_64 = "thal64";
        /**
         * <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i><br>
         * enum values
         */
        public static interface thal_alignment_type {
            /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h:120</i> */
            public static final int thal_any = 1;
            /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h:121</i> */
            public static final int thal_end1 = 2;
            /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h:122</i> */
            public static final int thal_end2 = 3;
            /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h:123</i> */
            public static final int thal_hairpin = 4;
        };
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int CHAR_MIN = (int)0;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int _I32_MAX = (int)2147483647;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int USHRT_MAX = (int)0xffff;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int MB_LEN_MAX = (int)5;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int _I16_MAX = (int)32767;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final long UINT_MAX = (long)0xffffffffL;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int _UI16_MAX = (int)0xffff;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int CHAR_MAX = (int)0xff;
        public static final int THAL_MAX_ALIGN = (int)60;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final long _UI32_MAX = (long)0xffffffffL;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int LONG_MAX = (int)2147483647;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final long ULONG_MAX = (long)0xffffffffL;
        /**
         * define<br>
         * Conversion Error : null<br>
         * SKIPPED:<br>
         * <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i><br>
         * - 1
         */
        /**
         * define<br>
         * Conversion Error : null<br>
         * SKIPPED:<br>
         * <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i><br>
         * - 1
         */
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int SHRT_MAX = (int)32767;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final long LLONG_MAX = (long)9223372036854775807L;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int SHRT_MIN = (int)(-32768);
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int UCHAR_MAX = (int)0xff;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final long SIZE_MAX = (long)0xffffffffL;
        /**
         * define<br>
         * Conversion Error : null<br>
         * SKIPPED:<br>
         * <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i><br>
         * - 1
         */
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int _UI8_MAX = (int)0xff;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final long _UI64_MAX = (long)0xffffffffffffffffL;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int CHAR_BIT = (int)8;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int INT_MIN = (int)(-2147483647 - 1);
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int SCHAR_MAX = (int)127;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int INT_MAX = (int)2147483647;
        /**
         * define<br>
         * Conversion Error : null<br>
         * SKIPPED:<br>
         * <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i><br>
         * - 1
         */
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final long ULLONG_MAX = (long)0xffffffffffffffffL;
        /**
         * define<br>
         * Conversion Error : null<br>
         * SKIPPED:<br>
         * <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i><br>
         * - 1
         */
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int SCHAR_MIN = (int)(-128);
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final long _I64_MAX = (long)9223372036854775807L;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int _I8_MAX = (int)127;
        public static final int THAL_MAX_SEQ = (int)10000;
        /** <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h</i> */
        public static final int LONG_MIN = (int)(-2147483647 - 1);
        /**
         * Original signature : <code>void set_thal_default_args(thal_args*)</code><br>
         * <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h:154</i>
         */
        void set_thal_default_args(thal_args a);
        /**
         * Original signature : <code>void set_thal_oligo_default_args(thal_args*)</code><br>
         * <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h:155</i>
         */
        void set_thal_oligo_default_args(thal_args a);
        /**
         * Original signature : <code>int get_thermodynamic_values(const char*, thal_results*)</code><br>
         * <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h:169</i><br>
         * @deprecated use the safer methods {@link #get_thermodynamic_values(String, thal_results)} and {@link #get_thermodynamic_values(Pointer, thal_results)} instead
         */
        @Deprecated
        int get_thermodynamic_values(Pointer path, thal_results o);
        /**
         * Original signature : <code>int get_thermodynamic_values(const char*, thal_results*)</code><br>
         * <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h:169</i>
         */
        int get_thermodynamic_values(String path, thal_results o);
        /**
         * Original signature : <code>void destroy_thal_structures()</code><br>
         * <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h:171</i>
         */
        void destroy_thal_structures();
        /**
         * Original signature : <code>void thal(const unsigned char*, const unsigned char*, const thal_args*, thal_results*)</code><br>
         * <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h:179</i><br>
         * @deprecated use the safer methods {@link #thal(byte[], byte[], thal_args, thal_results)} and {@link #thal(Pointer, Pointer, thal_args, thal_results)} instead
         */
        @Deprecated
        void thal(Pointer oligo1, Pointer oligo2, thal_args a, thal_results o);
        /**
         * Original signature : <code>void thal(const unsigned char*, const unsigned char*, const thal_args*, thal_results*)</code><br>
         * <i>native declaration : C:\Program Files (x86)\Microsoft Visual Studio 9.0\VC\include\limits.h:179</i>
         */
        void thal(byte oligo1[], byte oligo2[], thal_args a, thal_results o);
        public static final GlobalDouble _INFINITY = new GlobalDouble(ThalLoad.JNA_NATIVE_LIB, "_INFINITY");
        public static final GlobalDouble ABSOLUTE_ZERO = new GlobalDouble(ThalLoad.JNA_NATIVE_LIB, "ABSOLUTE_ZERO");
        /**
         * the maximum size of loop that can be calculated;<br>
         * for larger loops formula must be implemented
         */
        public static final GlobalInt MAX_LOOP = new GlobalInt(ThalLoad.JNA_NATIVE_LIB, "MAX_LOOP");
        public static final GlobalInt MIN_LOOP = new GlobalInt(ThalLoad.JNA_NATIVE_LIB, "MIN_LOOP");
        public class thal_args extends Structure {
            /** if non zero, print debugging info to stderr */
            public int debug;
            /**
             * @see thal_alignment_type
             * one of the<br>
             * 1 THAL_ANY, (by default)<br>
             * 2 THAL_END1,<br>
             * 3 THAL_END2,<br>
             * 4 THAL_HAIRPIN<br>
             * C type : thal_alignment_type
             */
            public int type;
            /** maximum size of loop to consider; longer than 30 bp are not allowed */
            public int maxLoop;
            /** concentration of monovalent cations */
            public double mv;
            /** concentration of divalent cations */
            public double dv;
            /** concentration of dNTP-s */
            public double dntp;
            /** concentration of oligonucleotides */
            public double dna_conc;
            /** temperature from which hairpin structures will be calculated */
            public double temp;
            /** if non zero, print only temperature to stderr */
            public int temponly;
            /** if non zero, dimer structure is calculated */
            public int dimer;
            public thal_args() {
                super();
            }
            protected List<? > getFieldOrder() {
                return Arrays.asList("debug", "type", "maxLoop", "mv", "dv", "dntp", "dna_conc", "temp", "temponly", "dimer");
            }
            public static class ByReference extends thal_args implements Structure.ByReference {

            };
            public static class ByValue extends thal_args implements Structure.ByValue {

            };
        }
        public class thal_results extends Structure {
            /** C type : char[255] */
            public byte[] msg = new byte[255];
            public double temp;
            public int align_end_1;
            public int align_end_2;
            public thal_results() {
                super();
            }
            protected List<? > getFieldOrder() {
                return Arrays.asList("msg", "temp", "align_end_1", "align_end_2");
            }
            /** @param msg C type : char[255] */
            public thal_results(byte msg[], double temp, int align_end_1, int align_end_2) {
                super();
                if ((msg.length != this.msg.length))
                    throw new IllegalArgumentException("Wrong array size !");
                this.msg = msg;
                this.temp = temp;
                this.align_end_1 = align_end_1;
                this.align_end_2 = align_end_2;
            }
            public static class ByReference extends thal_results implements Structure.ByReference {

            };
            public static class ByValue extends thal_results implements Structure.ByValue {

            };
        }
    }
    public static void main(String[] args) {
        JNA_NATIVE_LIB = NativeLibrary.getInstance(Thal.THAL_64);
        INSTANCE64 = (Thal)Native.loadLibrary(Thal.THAL_64, Thal.class);
    }
}


