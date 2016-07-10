# Generated by the protocol buffer compiler.  DO NOT EDIT!

from google.protobuf import descriptor
from google.protobuf import message
from google.protobuf import reflection
#from google.protobuf import descriptor_pb2
# @@protoc_insertion_point(imports)



DESCRIPTOR = descriptor.FileDescriptor(
  name='UserHistory.proto',
  package='bfd.userhistory',
  serialized_pb='\n\x11UserHistory.proto\x12\x0f\x62\x66\x64.userhistory\".\n\x0cItemTimePair\x12\x0b\n\x03iid\x18\x01 \x02(\t\x12\x11\n\ttimestamp\x18\x02 \x02(\x03\",\n\nUserClient\x12\x11\n\ttimestamp\x18\x01 \x02(\x03\x12\x0b\n\x03uid\x18\x02 \x01(\t')




_ITEMTIMEPAIR = descriptor.Descriptor(
  name='ItemTimePair',
  full_name='bfd.userhistory.ItemTimePair',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    descriptor.FieldDescriptor(
      name='iid', full_name='bfd.userhistory.ItemTimePair.iid', index=0,
      number=1, type=9, cpp_type=9, label=2,
      has_default_value=False, default_value=unicode("", "utf-8"),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    descriptor.FieldDescriptor(
      name='timestamp', full_name='bfd.userhistory.ItemTimePair.timestamp', index=1,
      number=2, type=3, cpp_type=2, label=2,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  extension_ranges=[],
  serialized_start=38,
  serialized_end=84,
)


_USERCLIENT = descriptor.Descriptor(
  name='UserClient',
  full_name='bfd.userhistory.UserClient',
  filename=None,
  file=DESCRIPTOR,
  containing_type=None,
  fields=[
    descriptor.FieldDescriptor(
      name='timestamp', full_name='bfd.userhistory.UserClient.timestamp', index=0,
      number=1, type=3, cpp_type=2, label=2,
      has_default_value=False, default_value=0,
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
    descriptor.FieldDescriptor(
      name='uid', full_name='bfd.userhistory.UserClient.uid', index=1,
      number=2, type=9, cpp_type=9, label=1,
      has_default_value=False, default_value=unicode("", "utf-8"),
      message_type=None, enum_type=None, containing_type=None,
      is_extension=False, extension_scope=None,
      options=None),
  ],
  extensions=[
  ],
  nested_types=[],
  enum_types=[
  ],
  options=None,
  is_extendable=False,
  extension_ranges=[],
  serialized_start=86,
  serialized_end=130,
)

DESCRIPTOR.message_types_by_name['ItemTimePair'] = _ITEMTIMEPAIR
DESCRIPTOR.message_types_by_name['UserClient'] = _USERCLIENT

class ItemTimePair(message.Message):
  __metaclass__ = reflection.GeneratedProtocolMessageType
  DESCRIPTOR = _ITEMTIMEPAIR
  
  # @@protoc_insertion_point(class_scope:bfd.userhistory.ItemTimePair)

class UserClient(message.Message):
  __metaclass__ = reflection.GeneratedProtocolMessageType
  DESCRIPTOR = _USERCLIENT
  
  # @@protoc_insertion_point(class_scope:bfd.userhistory.UserClient)

# @@protoc_insertion_point(module_scope)