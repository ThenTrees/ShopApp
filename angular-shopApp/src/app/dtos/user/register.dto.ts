import { IsDate, IsNotEmpty, IsPhoneNumber, IsString } from 'class-validator';

export class RegisterDto {
  @IsString()
  @IsNotEmpty()
  fullname: string;

  @IsNotEmpty()
  @IsPhoneNumber()
  phone_number: string;

  @IsString()
  address: string;

  @IsString()
  @IsNotEmpty()
  password: string;

  @IsString()
  retype_password: string;

  @IsDate()
  date_of_birth: Date;
  is_accepted: boolean;
  facebook_account_id: number = 0;
  google_account_id: number = 0;
  role_id: number;

  constructor(data: any) {
    this.fullname = data.fullname;
    this.phone_number = data.phone_number;
    this.address = data.address;
    this.password = data.password;
    this.retype_password = data.retype_password;
    this.date_of_birth = data.date_of_birth;
    this.is_accepted = data.is_accepted;
    this.facebook_account_id = data.facebook_account_id || 0;
    this.google_account_id = data.google_account_id || 0;
    this.role_id = data.role_id || 2;
  }
}
