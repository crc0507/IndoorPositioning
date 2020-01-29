//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.google.zxing.oned.rss.expanded.decoders;

import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

final class GeneralAppIdDecoder {
    private final BitArray information;
    private final CurrentParsingState current = new CurrentParsingState();
    private final StringBuffer buffer = new StringBuffer();

    GeneralAppIdDecoder(BitArray information) {
        this.information = information;
    }

    String decodeAllCodes(StringBuffer buff, int initialPosition) throws NotFoundException {
        int currentPosition = initialPosition;
        String remaining = null;

        while(true) {
            DecodedInformation info = this.decodeGeneralPurposeField(currentPosition, remaining);
            String parsedFields = FieldParser.parseFieldsInGeneralPurpose(info.getNewString());
            buff.append(parsedFields);
            if(info.isRemaining()) {
                remaining = String.valueOf(info.getRemainingValue());
            } else {
                remaining = null;
            }

            if(currentPosition == info.getNewPosition()) {
                return buff.toString();
            }

            currentPosition = info.getNewPosition();
        }
    }

    private boolean isStillNumeric(int pos) {
        if(pos + 7 > this.information.size) {
            return pos + 4 <= this.information.size;
        } else {
            for(int i = pos; i < pos + 3; ++i) {
                if(this.information.get(i)) {
                    return true;
                }
            }

            return this.information.get(pos + 3);
        }
    }

    private DecodedNumeric decodeNumeric(int pos) {
        int numeric;
        if(pos + 7 > this.information.size) {
            numeric = this.extractNumericValueFromBitArray(pos, 4);
            return numeric == 0?new DecodedNumeric(this.information.size, 10, 10):new DecodedNumeric(this.information.size, numeric - 1, 10);
        } else {
            numeric = this.extractNumericValueFromBitArray(pos, 7);
            int digit1 = (numeric - 8) / 11;
            int digit2 = (numeric - 8) % 11;
            return new DecodedNumeric(pos + 7, digit1, digit2);
        }
    }

    int extractNumericValueFromBitArray(int pos, int bits) {
        return extractNumericValueFromBitArray(this.information, pos, bits);
    }

    static int extractNumericValueFromBitArray(BitArray information, int pos, int bits) {
        if(bits > 32) {
            throw new IllegalArgumentException("extractNumberValueFromBitArray can\'t handle more than 32 bits");
        } else {
            int value = 0;

            for(int i = 0; i < bits; ++i) {
                if(information.get(pos + i)) {
                    value |= 1 << bits - i - 1;
                }
            }

            return value;
        }
    }

    DecodedInformation decodeGeneralPurposeField(int pos, String remaining) {
        this.buffer.setLength(0);
        if(remaining != null) {
            this.buffer.append(remaining);
        }

        this.current.position = pos;
        DecodedInformation lastDecoded = this.parseBlocks();
        return lastDecoded != null && lastDecoded.isRemaining()?new DecodedInformation(this.current.position, this.buffer.toString(), lastDecoded.getRemainingValue()):new DecodedInformation(this.current.position, this.buffer.toString());
    }

    private DecodedInformation parseBlocks() {
        boolean isFinished;
        BlockParsedResult result;
        boolean positionChanged;
        do {
            int initialPosition = this.current.position;
            if(this.current.isAlpha()) {
                result = this.parseAlphaBlock();
                isFinished = result.isFinished();
            } else if(this.current.isIsoIec646()) {
                result = this.parseIsoIec646Block();
                isFinished = result.isFinished();
            } else {
                result = this.parseNumericBlock();
                isFinished = result.isFinished();
            }

            positionChanged = initialPosition != this.current.position;
        } while((positionChanged || isFinished) && !isFinished);

        return result.getDecodedInformation();
    }

    private BlockParsedResult parseNumericBlock() {
        while(this.isStillNumeric(this.current.position)) {
            DecodedNumeric numeric = this.decodeNumeric(this.current.position);
            this.current.position = numeric.getNewPosition();
            DecodedInformation information;
            if(numeric.isFirstDigitFNC1()) {
                if(numeric.isSecondDigitFNC1()) {
                    information = new DecodedInformation(this.current.position, this.buffer.toString());
                } else {
                    information = new DecodedInformation(this.current.position, this.buffer.toString(), numeric.getSecondDigit());
                }

                return new BlockParsedResult(information, true);
            }

            this.buffer.append(numeric.getFirstDigit());
            if(numeric.isSecondDigitFNC1()) {
                information = new DecodedInformation(this.current.position, this.buffer.toString());
                return new BlockParsedResult(information, true);
            }

            this.buffer.append(numeric.getSecondDigit());
        }

        if(this.isNumericToAlphaNumericLatch(this.current.position)) {
            this.current.setAlpha();
            this.current.position += 4;
        }

        return new BlockParsedResult(false);
    }

    private BlockParsedResult parseIsoIec646Block() {
        while(this.isStillIsoIec646(this.current.position)) {
            DecodedChar iso = this.decodeIsoIec646(this.current.position);
            this.current.position = iso.getNewPosition();
            if(iso.isFNC1()) {
                DecodedInformation information = new DecodedInformation(this.current.position, this.buffer.toString());
                return new BlockParsedResult(information, true);
            }

            this.buffer.append(iso.getValue());
        }

        if(this.isAlphaOr646ToNumericLatch(this.current.position)) {
            this.current.position += 3;
            this.current.setNumeric();
        } else if(this.isAlphaTo646ToAlphaLatch(this.current.position)) {
            if(this.current.position + 5 < this.information.size) {
                this.current.position += 5;
            } else {
                this.current.position = this.information.size;
            }

            this.current.setAlpha();
        }

        return new BlockParsedResult(false);
    }

    private BlockParsedResult parseAlphaBlock() {
        while(this.isStillAlpha(this.current.position)) {
            DecodedChar alpha = this.decodeAlphanumeric(this.current.position);
            this.current.position = alpha.getNewPosition();
            if(alpha.isFNC1()) {
                DecodedInformation information = new DecodedInformation(this.current.position, this.buffer.toString());
                return new BlockParsedResult(information, true);
            }

            this.buffer.append(alpha.getValue());
        }

        if(this.isAlphaOr646ToNumericLatch(this.current.position)) {
            this.current.position += 3;
            this.current.setNumeric();
        } else if(this.isAlphaTo646ToAlphaLatch(this.current.position)) {
            if(this.current.position + 5 < this.information.size) {
                this.current.position += 5;
            } else {
                this.current.position = this.information.size;
            }

            this.current.setIsoIec646();
        }

        return new BlockParsedResult(false);
    }

    private boolean isStillIsoIec646(int pos) {
        if(pos + 5 > this.information.size) {
            return false;
        } else {
            int fiveBitValue = this.extractNumericValueFromBitArray(pos, 5);
            if(fiveBitValue >= 5 && fiveBitValue < 16) {
                return true;
            } else if(pos + 7 > this.information.size) {
                return false;
            } else {
                int sevenBitValue = this.extractNumericValueFromBitArray(pos, 7);
                if(sevenBitValue >= 64 && sevenBitValue < 116) {
                    return true;
                } else if(pos + 8 > this.information.size) {
                    return false;
                } else {
                    int eightBitValue = this.extractNumericValueFromBitArray(pos, 8);
                    return eightBitValue >= 232 && eightBitValue < 253;
                }
            }
        }
    }

    private DecodedChar decodeIsoIec646(int pos) {
        int fiveBitValue = this.extractNumericValueFromBitArray(pos, 5);
        if(fiveBitValue == 15) {
            return new DecodedChar(pos + 5, '$');
        } else if(fiveBitValue >= 5 && fiveBitValue < 15) {
            return new DecodedChar(pos + 5, (char)(48 + fiveBitValue - 5));
        } else {
            int sevenBitValue = this.extractNumericValueFromBitArray(pos, 7);
            if(sevenBitValue >= 64 && sevenBitValue < 90) {
                return new DecodedChar(pos + 7, (char)(sevenBitValue + 1));
            } else if(sevenBitValue >= 90 && sevenBitValue < 116) {
                return new DecodedChar(pos + 7, (char)(sevenBitValue + 7));
            } else {
                int eightBitValue = this.extractNumericValueFromBitArray(pos, 8);
                switch(eightBitValue) {
                    case 232:
                        return new DecodedChar(pos + 8, '!');
                    case 233:
                        return new DecodedChar(pos + 8, '\"');
                    case 234:
                        return new DecodedChar(pos + 8, '%');
                    case 235:
                        return new DecodedChar(pos + 8, '&');
                    case 236:
                        return new DecodedChar(pos + 8, '\'');
                    case 237:
                        return new DecodedChar(pos + 8, '(');
                    case 238:
                        return new DecodedChar(pos + 8, ')');
                    case 239:
                        return new DecodedChar(pos + 8, '*');
                    case 240:
                        return new DecodedChar(pos + 8, '+');
                    case 241:
                        return new DecodedChar(pos + 8, ',');
                    case 242:
                        return new DecodedChar(pos + 8, '-');
                    case 243:
                        return new DecodedChar(pos + 8, '.');
                    case 244:
                        return new DecodedChar(pos + 8, '/');
                    case 245:
                        return new DecodedChar(pos + 8, ':');
                    case 246:
                        return new DecodedChar(pos + 8, ';');
                    case 247:
                        return new DecodedChar(pos + 8, '<');
                    case 248:
                        return new DecodedChar(pos + 8, '=');
                    case 249:
                        return new DecodedChar(pos + 8, '>');
                    case 250:
                        return new DecodedChar(pos + 8, '?');
                    case 251:
                        return new DecodedChar(pos + 8, '_');
                    case 252:
                        return new DecodedChar(pos + 8, ' ');
                    default:
                        throw new RuntimeException("Decoding invalid ISO/IEC 646 value: " + eightBitValue);
                }
            }
        }
    }

    private boolean isStillAlpha(int pos) {
        if(pos + 5 > this.information.size) {
            return false;
        } else {
            int fiveBitValue = this.extractNumericValueFromBitArray(pos, 5);
            if(fiveBitValue >= 5 && fiveBitValue < 16) {
                return true;
            } else if(pos + 6 > this.information.size) {
                return false;
            } else {
                int sixBitValue = this.extractNumericValueFromBitArray(pos, 6);
                return sixBitValue >= 16 && sixBitValue < 63;
            }
        }
    }

    private DecodedChar decodeAlphanumeric(int pos) {
        int fiveBitValue = this.extractNumericValueFromBitArray(pos, 5);
        if(fiveBitValue == 15) {
            return new DecodedChar(pos + 5, '$');
        } else if(fiveBitValue >= 5 && fiveBitValue < 15) {
            return new DecodedChar(pos + 5, (char)(48 + fiveBitValue - 5));
        } else {
            int sixBitValue = this.extractNumericValueFromBitArray(pos, 6);
            if(sixBitValue >= 32 && sixBitValue < 58) {
                return new DecodedChar(pos + 6, (char)(sixBitValue + 33));
            } else {
                switch(sixBitValue) {
                    case 58:
                        return new DecodedChar(pos + 6, '*');
                    case 59:
                        return new DecodedChar(pos + 6, ',');
                    case 60:
                        return new DecodedChar(pos + 6, '-');
                    case 61:
                        return new DecodedChar(pos + 6, '.');
                    case 62:
                        return new DecodedChar(pos + 6, '/');
                    default:
                        throw new RuntimeException("Decoding invalid alphanumeric value: " + sixBitValue);
                }
            }
        }
    }

    private boolean isAlphaTo646ToAlphaLatch(int pos) {
        if(pos + 1 > this.information.size) {
            return false;
        } else {
            for(int i = 0; i < 5 && i + pos < this.information.size; ++i) {
                if(i == 2) {
                    if(!this.information.get(pos + 2)) {
                        return false;
                    }
                } else if(this.information.get(pos + i)) {
                    return false;
                }
            }

            return true;
        }
    }

    private boolean isAlphaOr646ToNumericLatch(int pos) {
        if(pos + 3 > this.information.size) {
            return false;
        } else {
            for(int i = pos; i < pos + 3; ++i) {
                if(this.information.get(i)) {
                    return false;
                }
            }

            return true;
        }
    }

    private boolean isNumericToAlphaNumericLatch(int pos) {
        if(pos + 1 > this.information.size) {
            return false;
        } else {
            for(int i = 0; i < 4 && i + pos < this.information.size; ++i) {
                if(this.information.get(pos + i)) {
                    return false;
                }
            }

            return true;
        }
    }
}
