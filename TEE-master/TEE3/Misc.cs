﻿using System;
using System.IO;
using System.Collections.Generic;

namespace TEE3
{
    internal enum Effect : byte
    {
        None = 0,
        Half = 5,
        Double = 20
    }

    internal class Entry
    {
        public byte Type;
        public Effect Effectiveness;
        public bool Foresight;
    }

    public static class Tasks
    {
        public static int FindFreeSpace(byte[] buffer, int length, uint searchStart, byte fsByte = 0xFF)
        {
            // Build array with FS bytes
            byte[] search = new byte[length];
            for (int i = 0; i < length; i++) search[i] = fsByte;

            // And find it
            if (length > 1)
            {
                int offset = FindBytes(buffer, search, searchStart);
                return offset;
            }
            else // Assume 1 byte needed
            {
                int offset = -1;
                for (int x = 0; x < buffer.Length; x++)
                {
                    if (buffer[x] == fsByte)
                    {
                        offset = x;
                        break;
                    }
                }
                return offset;
            }
        }

        public static int FindBytes(byte[] buffer, byte[] search, uint searchStart = 0)
        {
            // safe-ify~
            int me = (int)searchStart;
            //if (searchStart > 4) me = (int)(searchStart - (searchStart % 4));

            int offset = -1;
            bool found = false;
            while (!(me == buffer.Length - search.Length | offset != -1 | me == buffer.Length))
            {
                if (buffer[me] == search[0] & buffer[me + 1] == search[1])
                {
                    found = true;
                    int pos = 0;
                    while (!(pos == search.Length || found == false))
                    {
                        if (buffer[me + pos] != search[pos])
                        {
                            found = false;
                        }
                        pos += 1;
                    }

                    if (found)
                    {
                        offset = me;
                    }
                    else
                    {
                        offset = -1;
                    }
                }
                me += 4;
            }

            return offset;
        }

        public static int[] FindAndReplace(string rom, byte[] search, byte[] replacement)
        {
            // Safe-checking
            if (search.Length != replacement.Length) throw new Exception("Search and replace need to be the same length!");

            // Load ROM
            byte[] buffer = File.ReadAllBytes(rom);
            List<int> matches = new List<int>();

            // Perform find and repalce
            int check = 0; uint searchPos = 0;
            while (searchPos < (buffer.Length - search.Length) &&
                (check = FindBytes(buffer, search, searchPos)) != -1) // Finds next area and keeps it inbounds
            {
                // Repalce it
                for (int k = 0; k < replacement.Length; k++)
                {
                    buffer[check + k] = replacement[k];
                }

                // And move forward
                matches.Add(check);
                searchPos = (uint)(check + replacement.Length);
            }

            // Save ROM, and return
            File.WriteAllBytes(rom, buffer);
            return matches.ToArray();
        }

        public static int[] FindAndReplacePointer(string rom, uint oldOffset, uint newOffset)
        {
            // Build, and go
            byte[] oldPointer = BitConverter.GetBytes(oldOffset + 0x08000000);
            byte[] newPointer = BitConverter.GetBytes(newOffset + 0x08000000);
            return FindAndReplace(rom, oldPointer, newPointer);
        }
    }
}
